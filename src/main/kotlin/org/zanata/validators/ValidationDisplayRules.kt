package org.zanata.validators

import org.zanata.validators.ValidationAction.State

/**
 * Holds display rules of this validation according to the state
 *
 * @author Alex Eng [aeng@redhat.com](mailto:aeng@redhat.com)
 */
class ValidationDisplayRules {
    var isEnabled: Boolean = false
    var isLocked: Boolean = false

    private constructor() {}

    constructor(state: State) {
        updateRules(state)
    }

    /**
     * Update validation state Off : enabled = false, locked = false; Warning :
     * enabled = true, locked = false; Error : enabled = true, locked = true;
     */
    fun updateRules(state: State) {
        if (state === State.Off) {
            isEnabled = false
            isLocked = false
        } else if (state === State.Warning) {
            isEnabled = true
            isLocked = false
        } else if (state === State.Error) {
            isEnabled = true
            isLocked = true
        }
    }
}
