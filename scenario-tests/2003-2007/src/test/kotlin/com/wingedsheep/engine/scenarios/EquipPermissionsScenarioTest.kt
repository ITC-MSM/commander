package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.Bonesplitter
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.EquipAbilitiesAtInstantSpeed
import com.wingedsheep.sdk.scripting.FreeFirstEquipEachTurn
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * Equip-timing/cost permissions added for Forge Anew (CR 702.6e equip timing is lifted, plus a
 * free-first-equip-per-turn discount). Exercised here with the real Mirrodin equip {1} card
 * (Bonesplitter) plus an inline "Test Forge" enchantment that grants the two permissions, so the
 * engine behaviour is pinned independently of the LTR card itself.
 */
class EquipPermissionsScenarioTest : FunSpec({

    // Mirrors Forge Anew's two static clauses: instant-speed equip "during your turn", and a
    // free-first-equip-each-turn discount.
    val testForge = card("Test Forge") {
        manaCost = "{2}{W}"
        typeLine = "Enchantment"
        oracleText = "test"
        staticAbility {
            condition = Conditions.IsYourTurn
            ability = EquipAbilitiesAtInstantSpeed
        }
        staticAbility {
            ability = FreeFirstEquipEachTurn
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + Bonesplitter + testForge)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    val equipId = Bonesplitter.activatedAbilities.first().id

    test("without a permission, equip is sorcery-speed: it can't be activated during combat") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val courser = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        val sword = driver.putPermanentOnBattlefield(you, "Bonesplitter")

        driver.giveColorlessMana(you, 1)
        driver.passPriorityUntil(Step.BEGIN_COMBAT)

        // Equip during combat with no instant-speed grant — rejected by the timing rule.
        driver.submitExpectFailure(
            ActivateAbility(you, sword, equipId, targets = listOf(ChosenTarget.Permanent(courser)))
        )
        driver.state.getEntity(sword)?.get<AttachedToComponent>().shouldBeNull()
    }

    test("EquipAbilitiesAtInstantSpeed lets you equip during combat on your turn") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val courser = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        val sword = driver.putPermanentOnBattlefield(you, "Bonesplitter")
        driver.putPermanentOnBattlefield(you, "Test Forge")

        driver.giveColorlessMana(you, 1)
        driver.passPriorityUntil(Step.BEGIN_COMBAT)

        driver.submit(
            ActivateAbility(you, sword, equipId, targets = listOf(ChosenTarget.Permanent(courser)))
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.state.getEntity(sword)?.get<AttachedToComponent>()?.targetId shouldBe courser
    }

    test("FreeFirstEquipEachTurn: first equip each turn is free, the next costs full") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val a = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        val b = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        val sword = driver.putPermanentOnBattlefield(you, "Bonesplitter")
        driver.putPermanentOnBattlefield(you, "Test Forge")

        // No mana available — the first equip this turn is still payable (it's free).
        driver.submit(
            ActivateAbility(you, sword, equipId, targets = listOf(ChosenTarget.Permanent(a)))
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.state.getEntity(sword)?.get<AttachedToComponent>()?.targetId shouldBe a

        // Second equip this turn must pay the real {1} cost — with no mana it fails.
        driver.submitExpectFailure(
            ActivateAbility(you, sword, equipId, targets = listOf(ChosenTarget.Permanent(b)))
        )
        driver.state.getEntity(sword)?.get<AttachedToComponent>()?.targetId shouldBe a

        // Pay the {1} and the second equip resolves.
        driver.giveColorlessMana(you, 1)
        driver.submit(
            ActivateAbility(you, sword, equipId, targets = listOf(ChosenTarget.Permanent(b)))
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.state.getEntity(sword)?.get<AttachedToComponent>()?.targetId shouldBe b
    }
})
