package org.apache.commons.digester3;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static java.lang.String.format;
import static java.util.Arrays.fill;
import static net.sf.cglib.proxy.Enhancer.isEnhanced;
import static org.apache.commons.beanutils.ConstructorUtils.getAccessibleConstructor;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.LazyLoader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Rule implementation that creates a new object and pushes it onto the object stack. When the element is complete, the
 * object will be popped
 */
public class ObjectCreateRule
    extends Rule
{

    // ----------------------------------------------------------- Constructors

    /**
     * Construct an object create rule with the specified class name.
     *
     * @param className Java class name of the object to be created
     */
    public ObjectCreateRule( String className )
    {
        this( className, (String) null );
    }

    /**
     * Construct an object create rule with the specified class.
     *
     * @param clazz Java class name of the object to be created
     */
    public ObjectCreateRule( Class<?> clazz )
    {
        this( clazz.getName(), (String) null );
        this.clazz = clazz;
    }

    /**
     * Construct an object create rule with the specified class name and an optional attribute name containing an
     * override.
     *
     * @param className Java class name of the object to be created
     * @param attributeName Attribute name which, if present, contains an override of the class name to create
     */
    public ObjectCreateRule( String className, String attributeName )
    {
        this.className = className;
        this.attributeName = attributeName;
    }

    /**
     * Construct an object create rule with the specified class and an optional attribute name containing an override.
     *
     * @param attributeName Attribute name which, if present, contains an
     * @param clazz Java class name of the object to be created override of the class name to create
     */
    public ObjectCreateRule( String attributeName, Class<?> clazz )
    {
        this( clazz.getName(), attributeName );
        this.clazz = clazz;
    }

    // ----------------------------------------------------- Instance Variables

    /**
     * The attribute containing an override class name if it is present.
     */
    protected String attributeName = null;

    /**
     * The Java class of the object to be created.
     */
    protected Class<?> clazz = null;

    /**
     * The Java class name of the object to be created.
     */
    protected String className = null;

    /**
     * The constructor arguments - order is preserved by the LinkedHashMap
     *
     * @since 3.2
     */
    private Class<?>[] constructorArgumentsTypes;

    /**
     * cglib Factory for lazily-loaded instances after the first.
     * Only used in the presence of constructor args.
     *
     * @since 3.2
     */
    private Factory proxyFactory;

    // --------------------------------------------------------- Public Methods

    /**
     * Allows users specify constructor arguments.
     *
     * @since 3.2
     */
    public void setConstructorArguments( Class<?>...constructorArgumentsTypes )
    {
        if ( constructorArgumentsTypes == null )
        {
            throw new IllegalArgumentException( "Parameter 'constructorArgumentsTypes' must not be null" );
        }

        this.constructorArgumentsTypes = constructorArgumentsTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin( String namespace, String name, Attributes attributes )
        throws Exception
    {
        Class<?> clazz = this.clazz;

        if ( clazz == null )
        {
            // Identify the name of the class to instantiate
            String realClassName = className;
            if ( attributeName != null )
            {
                String value = attributes.getValue( attributeName );
                if ( value != null )
                {
                    realClassName = value;
                }
            }
            if ( getDigester().getLogger().isDebugEnabled() )
            {
                getDigester().getLogger().debug( format( "[ObjectCreateRule]{%s} New '%s'",
                                                         getDigester().getMatch(),
                                                         realClassName ) );
            }

            // Instantiate the new object and push it on the context stack
            clazz = getDigester().getClassLoader().loadClass( realClassName );
        }
        Object instance;
        if ( constructorArgumentsTypes == null || constructorArgumentsTypes.length == 0 )
        {
            if ( getDigester().getLogger().isDebugEnabled() )
            {
                getDigester().getLogger().debug( format( "[ObjectCreateRule]{%s} New '%s' using default empty constructor",
                                                         getDigester().getMatch(),
                                                         clazz.getName() ) );
            }

            instance = clazz.newInstance();
        }
        else
        {
            Constructor<?> constructor = getAccessibleConstructor( clazz, constructorArgumentsTypes );

            if ( constructor == null )
            {
                throw new SAXException( format( "[ObjectCreateRule]{%s} Class '%s' does not have a construcor with types",
                                                getDigester().getMatch(),
                                                clazz.getName(),
                                                Arrays.toString( constructorArgumentsTypes ) ) );
            }

            instance = createLazyProxy( constructor );
        }
        getDigester().push( instance );
    }

    private Object createLazyProxy( Constructor<?> constructor ) {
        Object[] constructorArguments = new Object[constructorArgumentsTypes.length];
        fill( constructorArguments, null );
        getDigester().pushParams( constructorArguments );

        ObjectCreateRuleLazyLoader lazyLoader = new ObjectCreateRuleLazyLoader( constructor,
                                                                                constructorArgumentsTypes,
                                                                                constructorArguments );
        if ( proxyFactory == null ) {
            synchronized ( this ) {
                // check again for null now that we're in the synchronized block:
                if ( proxyFactory == null ) {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass( clazz );
                    enhancer.setCallback( lazyLoader );
                    enhancer.setClassLoader( getDigester().getClassLoader() );
                    Object result = enhancer.create();
                    proxyFactory = (Factory) result;
                    return result;
                }
            }
        }
        return proxyFactory.newInstance( lazyLoader );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end( String namespace, String name )
        throws Exception
    {
        Object top = getDigester().pop();

        if ( isEnhanced( top.getClass() ) )
        {
            // do lazy load?!?
            getDigester().popParams();
        }

        if ( getDigester().getLogger().isDebugEnabled() )
        {
            getDigester().getLogger().debug( format( "[ObjectCreateRule]{%s} Pop '%s'",
                                                     getDigester().getMatch(),
                                                     top.getClass().getName() ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return format( "ObjectCreateRule[className=%s, attributeName=%s]", className, attributeName );
    }

}
