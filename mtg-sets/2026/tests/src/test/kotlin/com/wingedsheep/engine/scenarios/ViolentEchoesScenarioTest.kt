package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Violent Echoes (Reality Fracture #95) — {2}{R}{R} Instant:
 *   Violent Echoes deals 6 damage to target creature or planeswalker. If excess damage was dealt to
 *   that permanent this way, empower Jace X, where X is that excess damage.
 *
 * Pins `DealDamage(excessDamageVariable = ...)` for a creature (above lethal, counting damage
 * already marked) and a planeswalker (above loyalty, CR 120.4a), and the no-excess case.
 */
class ViolentEchoesScenarioTest : ScenarioTestBase() {

    private fun ScenarioTestBase.TestGame.jaceTokenLoyalty(): Int? =
        findPermanents("Jace").singleOrNull()
            ?.let { state.getEntity(it)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) }

    private fun game(vararg opponentPermanents: String): ScenarioTestBase.TestGame {
        var builder = scenario().withPlayers()
            .withCardInHand(1, "Violent Echoes")
            .withCardInHand(1, "Shock")
            .withLandsOnBattlefield(1, "Mountain", 6)
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(2, "Island")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        for (p in opponentPermanents) builder = builder.withCardOnBattlefield(2, p)
        return builder.build()
    }

    init {
        test("6 damage to a 2/2 is 4 excess: empower Jace 4") {
            val game = game("Grizzly Bears")
            game.castSpell(1, "Violent Echoes", game.findPermanent("Grizzly Bears")!!).error shouldBe null
            game.resolveStack()

            game.isOnBattlefield("Grizzly Bears") shouldBe false
            game.jaceTokenLoyalty() shouldBe 4
        }

        test("damage already marked counts toward lethal, so more of the 6 is excess") {
            val game = game("Craw Wurm") // 6/4
            val wurm = game.findPermanent("Craw Wurm")!!
            game.castSpell(1, "Shock", wurm).error shouldBe null
            game.resolveStack()
            game.castSpell(1, "Violent Echoes", wurm).error shouldBe null
            game.resolveStack()

            game.isOnBattlefield("Craw Wurm") shouldBe false
            // Lethal needed 2 more; 4 excess.
            game.jaceTokenLoyalty() shouldBe 4
        }

        test("no excess damage: no Jace token is created") {
            val game = game("Colossal Dreadmaw") // 6/6
            game.castSpell(1, "Violent Echoes", game.findPermanent("Colossal Dreadmaw")!!).error shouldBe null
            game.resolveStack()

            game.isOnBattlefield("Colossal Dreadmaw") shouldBe false
            game.findPermanents("Jace") shouldBe emptyList()
        }

        test("against a planeswalker, excess is the damage above its loyalty") {
            val game = game("Jace Beleren") // loyalty 3
            game.castSpell(1, "Violent Echoes", game.findPermanent("Jace Beleren")!!).error shouldBe null
            game.resolveStack()

            game.isOnBattlefield("Jace Beleren") shouldBe false
            game.jaceTokenLoyalty() shouldBe 3
        }
    }
}
