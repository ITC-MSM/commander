package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.AppliedGeometry
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Applied Geometry (SOS) — {2}{G}{U} Sorcery.
 *
 * "Create a token that's a copy of target non-Aura permanent you control, except it's a 0/0 Fractal
 *  creature in addition to its other types. Put six +1/+1 counters on it."
 *
 * Exercises the executor change that publishes [CreateTokenCopyOfTarget]'s created token under the
 * shared `CREATED_TOKENS` pipeline collection, so the following `AddCountersToCollection` puts the
 * six +1/+1 counters on exactly the token just made.
 */
class AppliedGeometryScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + AppliedGeometry)
        return driver
    }

    fun newTokens(driver: GameTestDriver, playerId: EntityId): List<EntityId> =
        driver.state.getZone(ZoneKey(playerId, Zone.BATTLEFIELD)).filter { id ->
            driver.state.getEntity(id)?.get<TokenComponent>() != null
        }

    test("copy of a creature you control is a 0/0 Fractal with six +1/+1 counters") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Source permanent to copy: a vanilla 2/2.
        val source = driver.putCreatureOnBattlefield(player, "Black Creature")

        val spell = driver.putCardInHand(player, "Applied Geometry")
        driver.giveColorlessMana(player, 2)
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.BLUE, 1)
        driver.submit(
            CastSpell(
                playerId = player,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(source)),
            ),
        )
        driver.bothPass()

        val tokens = newTokens(driver, player)
        tokens.size shouldBe 1
        val token = tokens.single()

        // Six +1/+1 counters landed on the created copy.
        val counters = driver.state.getEntity(token)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
        counters shouldBe 6

        // It is a Fractal creature.
        val projected = driver.state.projectedState
        projected.isCreature(token).shouldBeTrue()
        projected.getSubtypes(token).contains(Subtype.FRACTAL.value).shouldBeTrue()

        // 0/0 base + six +1/+1 counters → 6/6 effective.
        projected.getPower(token) shouldBe 6
        projected.getToughness(token) shouldBe 6
    }

    test("copy of a noncreature artifact you control becomes a creature in addition to its types") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // A noncreature permanent: a Treasure-like artifact stand-in via a token card is awkward,
        // so use a real noncreature artifact from the test corpus.
        val source = driver.putPermanentOnBattlefield(player, "Mox Sapphire")

        val spell = driver.putCardInHand(player, "Applied Geometry")
        driver.giveColorlessMana(player, 2)
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.BLUE, 1)
        driver.submit(
            CastSpell(
                playerId = player,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(source)),
            ),
        )
        driver.bothPass()

        val token = newTokens(driver, player).single()
        val projected = driver.state.projectedState

        // "in addition to its other types": still an artifact, now also a creature.
        projected.isCreature(token).shouldBeTrue()
        driver.state.getEntity(token)?.get<CardComponent>()?.name shouldBe "Mox Sapphire"
        projected.getPower(token)!! shouldBeGreaterThan 0 // 0 base + 6 counters
    }
})
