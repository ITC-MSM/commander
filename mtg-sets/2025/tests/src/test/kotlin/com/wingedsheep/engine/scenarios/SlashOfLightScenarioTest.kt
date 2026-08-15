package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SlashOfLight
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Slash of Light (FIN #32) — {1}{W} Instant.
 *
 *   "Slash of Light deals damage equal to the number of creatures you control plus the
 *    number of Equipment you control to target creature."
 *
 * Verifies the summed dynamic damage (creatures + Equipment you control).
 */
class SlashOfLightScenarioTest : FunSpec({

    // A simple Equipment artifact to count toward the damage.
    val testEquipment = card("Test Sword") {
        manaCost = "{1}"
        typeLine = "Artifact — Equipment"
        oracleText = "Equip {1}"
        equipAbility("{1}")
    }

    // A plain (non-Equipment) artifact that must NOT count toward the damage.
    val plainArtifact = card("Test Trinket") {
        manaCost = "{1}"
        typeLine = "Artifact"
        oracleText = ""
    }

    // A high-toughness target so it survives and we can read the marked damage exactly.
    val bigWall = card("Big Wall") {
        manaCost = "{4}"
        typeLine = "Creature — Wall"
        power = 0
        toughness = 10
        oracleText = ""
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SlashOfLight, testEquipment, plainArtifact, bigWall))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        return driver
    }

    fun GameTestDriver.markedDamage(id: EntityId): Int =
        state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    test("deals damage equal to creatures + Equipment you control") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // 2 creatures and 1 Equipment under my control -> expected 3 damage.
        driver.putCreatureOnBattlefield(me, "Centaur Courser")
        driver.putCreatureOnBattlefield(me, "Centaur Courser")
        driver.putPermanentOnBattlefield(me, "Test Sword")

        val target = driver.putCreatureOnBattlefield(opp, "Big Wall")

        val slash = driver.putCardInHand(me, "Slash of Light")
        driver.giveMana(me, Color.WHITE, 2)

        driver.castSpell(me, slash, listOf(target)).isSuccess shouldBe true
        driver.bothPass()

        driver.markedDamage(target) shouldBe 3
    }

    test("counts only Equipment, not other artifacts; only creatures you control") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // 1 of my creatures + 1 Equipment = 2. Opponent's creature and a non-Equipment
        // artifact must not count.
        driver.putCreatureOnBattlefield(me, "Centaur Courser")
        driver.putPermanentOnBattlefield(me, "Test Sword")
        driver.putCreatureOnBattlefield(opp, "Centaur Courser") // not mine
        driver.putPermanentOnBattlefield(me, "Test Trinket")    // artifact, not Equipment

        val target = driver.putCreatureOnBattlefield(opp, "Big Wall")

        val slash = driver.putCardInHand(me, "Slash of Light")
        driver.giveMana(me, Color.WHITE, 2)

        driver.castSpell(me, slash, listOf(target)).isSuccess shouldBe true
        driver.bothPass()

        driver.markedDamage(target) shouldBe 2
    }
})
