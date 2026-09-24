package com.wingedsheep.engine.mechanics.sba.player

import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.mechanics.sba.StateBasedActionCheck
import com.wingedsheep.engine.mechanics.sba.StateBasedActionModule

class PlayerSbaModule(private val zones: ZoneTransitionService) : StateBasedActionModule {
    override fun checks(): List<StateBasedActionCheck> = listOf(
        StartYourEnginesCheck(),
        AscendCitysBlessingCheck(),
        StoriedEnduringStoryCheck(),
        PlayerLifeLossCheck(),
        CommanderDamageLossCheck(),
        PoisonLossCheck(),
        TeamLossPropagationCheck(),
        PlayerLeavesGameCheck(zones)
    )
}
