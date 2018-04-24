package org.zanata.validators

/**
 * @author Sean Flanigan [sflaniga@redhat.com](mailto:sflaniga@redhat.com)
 */
// see https://stackoverflow.com/a/7891968/14379
// and https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Proxy

// Note that the runtime function name may be mangled (eg "_mhpeer$" appended)
// which will affect the fake string, unless you mark the interface/method as external (JS only).
actual val fakeValidationMessages: dynamic = js("""
    (function () {
        function asFunction(s) {
            return function() {
                var args = Array.from(arguments)
                if (args && args.length) {
                    // remove mangling from method name (starting from final underscore)
                    return s.replace(/_[^_]+${'$'}/, "") + ": [" + args + "]"
                } else return s
            }
        }
        return new Proxy({}, {
            get: function(target, name) {
                return asFunction(name)
            }
        })
    }())
""")
