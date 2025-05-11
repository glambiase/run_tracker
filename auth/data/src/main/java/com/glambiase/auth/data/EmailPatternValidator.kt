package com.glambiase.auth.data

import android.util.Patterns
import com.glambiase.auth.domain.PatternValidator

object EmailPatternValidator : PatternValidator {
    override fun matches(value: String) = Patterns.EMAIL_ADDRESS.matcher(value).matches()
}