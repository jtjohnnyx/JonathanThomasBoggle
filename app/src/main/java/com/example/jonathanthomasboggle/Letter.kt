package com.example.jonathanthomasboggle

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class Letter(@IdRes val letterId: Int, var letter: Char, @DrawableRes var imageId: Int, var chosen: Boolean)
