package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.CaseOfTheGorgonsKiss
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Case of the Gorgon's Kiss — {B} Enchantment — Case.
 *
 * The "to solve" clause is the new engine vocabulary: a *game-wide* count of creature cards that
 * reached graveyards this turn, which brings two printed rulings with it — tokens never count, and
 * the check reads what the card is in the graveyard. The Solved line is the other half worth
 * pinning: the Case animates itself without ceasing to be an enchantment.
 */
class CaseOfTheGorgonsKissScenarioTest : FunSpec({

    /** {1} sorcery: destroy all creatures — the cheapest way to fill graveyards on demand. */
    val testWrath = card("Test Purge") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        oracleText = "Destroy all creatures."
        spell {
            effect = Effects.DestroyAll(GameObjectFilter.Creature)
        }
    }

    /** {1} sorcery: make two 1/1 tokens, so the token exclusion can be exercised. */
    val testTokens = card("Test Deploy") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        oracleText = "Create two 1/1 white Soldier creature tokens."
        spell {
            effect = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Soldier"),
                count = 2,
                name = "Soldier"
            )
        }
    }

    /** {1} sorcery: 1 damage to target creature, to make it a legal Gorgon's Kiss target. */
    val testSpark = card("Test Spark") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        oracleText = "Test Spark deals 1 damage to target creature."
        spell {
            val t = target(
                "target",
                com.wingedsheep.sdk.scripting.targets.TargetCreature(
                    filter = com.wingedsheep.sdk.scripting.filters.unified.TargetFilter.Creature
                )
            )
            effect = Effects.DealDamage(1, t)
        }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CaseOfTheGorgonsKiss)
        driver.registerCard(testWrath)
        driver.registerCard(testTokens)
        driver.registerCard(testSpark)
        driver.initMirrorMatch(Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isSolved(id: EntityId): Boolean =
        state.getEntity(id)?.has<SolvedComponent>() == true

    fun GameTestDriver.castSorcery(name: String) {
        val spell = putCardInHand(player1, name)
        giveColorlessMana(player1, 1)
        castSpell(player1, spell).isSuccess shouldBe true
        bothPass()
    }

    test("the enters trigger destroys a damaged creature, and only damaged ones are legal targets") {
        val driver = newDriver()
        val damaged = driver.putCreatureOnBattlefield(driver.player2, "Force of Nature") // 5/5
        val untouched = driver.putCreatureOnBattlefield(driver.player2, "Centaur Courser") // 3/3

        val spark = driver.putCardInHand(driver.player1, "Test Spark")
        driver.giveColorlessMana(driver.player1, 1)
        driver.castSpell(driver.player1, spark, listOf(damaged)).isSuccess shouldBe true
        driver.bothPass()

        val card = driver.putCardInHand(driver.player1, "Case of the Gorgon's Kiss")
        driver.giveMana(driver.player1, Color.BLACK, 1)
        driver.castSpell(driver.player1, card).isSuccess shouldBe true
        driver.bothPass() // the Case resolves; its enters trigger asks for a target

        val decision = driver.pendingDecision
        driver.submitTargetSelection(driver.player1, listOf(damaged))
        driver.bothPass()

        decision shouldNotBe null
        driver.state.getBattlefield().contains(damaged) shouldBe false
        driver.state.getBattlefield().contains(untouched) shouldBe true
    }

    test("three creature cards reaching graveyards solve it; tokens don't count") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Gorgon's Kiss")

        // Two token creatures plus one card creature, all destroyed: only the card counts.
        driver.castSorcery("Test Deploy")
        driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.castSorcery("Test Purge")

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe false

        // Next own turn: three card creatures die at once.
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        driver.castSorcery("Test Purge")

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true
    }

    test("Solved — it becomes a 4/4 Gorgon with deathtouch and lifelink, and stays an enchantment") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Gorgon's Kiss")

        driver.state.projectedState.isCreature(case) shouldBe false

        driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        driver.castSorcery("Test Purge")
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true

        val projected = driver.state.projectedState
        projected.isCreature(case) shouldBe true
        projected.getPower(case) shouldBe 4
        projected.getToughness(case) shouldBe 4
        projected.hasKeyword(case, Keyword.DEATHTOUCH) shouldBe true
        projected.hasKeyword(case, Keyword.LIFELINK) shouldBe true
        projected.hasSubtype(case, "Gorgon") shouldBe true
        // "In addition to its other types" — it is still an enchantment Case.
        projected.hasType(case, "ENCHANTMENT") shouldBe true
    }
})
