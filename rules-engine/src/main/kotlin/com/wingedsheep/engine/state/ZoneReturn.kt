package com.wingedsheep.engine.state

import com.wingedsheep.sdk.core.Zone
import kotlinx.serialization.Serializable

@Serializable
data class ZoneReturn(val source: ObjectRef, val movedObject: ObjectRef, val previousZone: Zone)
