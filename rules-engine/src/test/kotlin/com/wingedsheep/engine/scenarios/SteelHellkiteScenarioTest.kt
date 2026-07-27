package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.som.cards.SteelHellkite
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Steel Hellkite (SOM #205, reprinted as FDN #681) — {6} 5/5 Artifact Creature — Dragon.
 *
 * "Flying
 *  {2}: This creature gets +1/+0 until end of turn.
 *  {X}: Destroy each nonland permanent with mana value X whose controller was dealt combat damage
 *  by this creature this turn. Activate only once each turn."
 *
 * Covers the new `ControllerDealtCombatDamageBySourceThisTurn` state predicate: the sweep is scoped
 * both by mana value (== X) and by which players the Hellkite actually connected with this turn,
 * and it is a no-op before it has hit anyone.
 */
class SteelHellkiteScenarioTest : FunSpec({

    val sweepAbilityId = SteelHellkite.activatedAbilities[1].id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(SteelHellkite)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Attack the opponent with [hellkite] unblocked so it connects for combat damage. */
    fun connectWithOpponent(driver: GameTestDriver, attacker: EntityId, hellkite: EntityId, defender: EntityId) {
        driver.removeSummoningSickness(hellkite)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(hellkite), defender).isSuccess shouldBe true
        driver.declareNoBlockers(defender)
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
    }

    fun sweep(driver: GameTestDriver, player: EntityId, hellkite: EntityId, x: Int) {
        driver.giveColorlessMana(player, x)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = hellkite, abilityId = sweepAbilityId, xValue = x)
        ).isSuccess shouldBe true
        driver.bothPass()
    }

    fun onBattlefield(driver: GameTestDriver, playerId: EntityId, entityId: EntityId): Boolean =
        driver.state.getZone(ZoneKey(playerId, Zone.BATTLEFIELD)).contains(entityId)

    test("destroys the damaged player's nonland permanents whose mana value equals X") {
        val driver = newDriver()
        val me = driver.player1
        val foe = driver.player2

        val hellkite = driver.putCreatureOnBattlefield(me, "Steel Hellkite")
        // Grizzly Bears is {1}{G} — mana value 2.
        val bears = driver.putCreatureOnBattlefield(foe, "Grizzly Bears")

        connectWithOpponent(driver, me, hellkite, foe)
        sweep(driver, me, hellkite, x = 2)

        onBattlefield(driver, foe, bears) shouldBe false
        driver.state.getZone(ZoneKey(foe, Zone.GRAVEYARD)).contains(bears) shouldBe true
    }

    test("spares permanents whose mana value is not exactly X") {
        val driver = newDriver()
        val me = driver.player1
        val foe = driver.player2

        val hellkite = driver.putCreatureOnBattlefield(me, "Steel Hellkite")
        val bears = driver.putCreatureOnBattlefield(foe, "Grizzly Bears") // mana value 2

        connectWithOpponent(driver, me, hellkite, foe)
        sweep(driver, me, hellkite, x = 3)

        onBattlefield(driver, foe, bears) shouldBe true
    }

    test("spares permanents controlled by players it did not connect with — including your own") {
        val driver = newDriver()
        val me = driver.player1
        val foe = driver.player2

        val hellkite = driver.putCreatureOnBattlefield(me, "Steel Hellkite")
        val myBears = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val theirBears = driver.putCreatureOnBattlefield(foe, "Grizzly Bears")

        connectWithOpponent(driver, me, hellkite, foe)
        sweep(driver, me, hellkite, x = 2)

        // The Hellkite dealt combat damage to the opponent, not to me.
        onBattlefield(driver, foe, theirBears) shouldBe false
        onBattlefield(driver, me, myBears) shouldBe true
    }

    test("activating before it has connected destroys nothing") {
        val driver = newDriver()
        val me = driver.player1
        val foe = driver.player2

        val hellkite = driver.putCreatureOnBattlefield(me, "Steel Hellkite")
        val bears = driver.putCreatureOnBattlefield(foe, "Grizzly Bears")

        sweep(driver, me, hellkite, x = 2)

        onBattlefield(driver, foe, bears) shouldBe true
    }

    test("the sweep can be activated only once each turn") {
        val driver = newDriver()
        val me = driver.player1
        val foe = driver.player2

        val hellkite = driver.putCreatureOnBattlefield(me, "Steel Hellkite")
        connectWithOpponent(driver, me, hellkite, foe)

        driver.giveColorlessMana(me, 10)
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        fun sweepOffered(): Boolean = enumerator.enumerate(driver.state, me, EnumerationMode.FULL)
            .any { (it.action as? ActivateAbility)?.abilityId == sweepAbilityId }

        sweepOffered() shouldBe true
        driver.submit(
            ActivateAbility(playerId = me, sourceId = hellkite, abilityId = sweepAbilityId, xValue = 1)
        ).isSuccess shouldBe true
        driver.bothPass()
        sweepOffered() shouldBe false
    }
})
