package org.zanata.validators

import java.lang.reflect.Proxy
import java.util.*

/**
 * @author Sean Flanigan [sflaniga@redhat.com](mailto:sflaniga@redhat.com)
 */
private val classLoader = object {}.javaClass.classLoader
actual val fakeValidationMessages: ValidationMessages =
        Proxy.newProxyInstance(classLoader,
                arrayOf<Class<*>>(ValidationMessages::class.java),
                { _, method, args ->
                    if (args == null || args.isEmpty()) {
                        return@newProxyInstance method.name
                    } else {
                        println(args[0])
                        return@newProxyInstance method.name + ": " + Arrays.asList<Any>(*args)
//                        return@newProxyInstance method.name + ": " + args
                    }
                }) as ValidationMessages
