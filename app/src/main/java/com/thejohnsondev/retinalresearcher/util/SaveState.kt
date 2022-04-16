package com.thejohnsondev.retinalresearcher.util

sealed class SaveState
object Saving: SaveState()
object Saved: SaveState()
