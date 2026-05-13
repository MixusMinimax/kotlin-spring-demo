package com.barmetler.springdemo.util.patch

import com.barmetler.springdemo.util.patch.Field.Missing
import com.barmetler.springdemo.util.patch.Field.Null
import com.barmetler.springdemo.util.patch.Field.Value

inline fun <T : Any> Field<T>.ifSet(block: (T?) -> Unit) = when (this) {
    is Missing -> Unit
    is Null -> block(null)
    is Value -> block(value)
}
