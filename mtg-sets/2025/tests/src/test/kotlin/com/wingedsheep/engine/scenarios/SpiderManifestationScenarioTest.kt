package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.spm.cards.SpiderManifestation
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * Spider Manifestation (SPM #148) — {1}{R/G} Creature — Spider Avatar, 2/2.
 *
 *  - Reach.
 *  - "{T}: Add {R} or {G}." — an unridered dual mana line, so it is *two* abilities sharing the tap
 *    cost rather than one choice-of-color ability. That distinction is what this test pins: the
 *    colour is picked by choosing which ability to activate, not by a decision on resolution, which
 *    is the spelling Assay reads out of the printed text and the one 165 other cards use.
 *  - "Whenever you cast a spell with mana value 4 or greater, untap this creature." — the rider that
 *    makes the mana line worth having, and the reason the threshold matters.
 */
class SpiderManifestationScenarioTest : FunSpec({

    val manaAbilities = SpiderManifestation.activatedAbilities.filter { it.isManaAbility }

    fun pool(driver: GameTestDriver, player: EntityId): ManaPoolComponent =
        driver.state.getEntity(player)?.get<ManaPoolComponent>() ?: ManaPoolComponent()

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20, skipMulligans = true)
        return driver
    }

    fun readySpider(driver: GameTestDriver, player: EntityId): EntityId {
        val spider = driver.putCreatureOnBattlefield(player, "Spider Manifestation")
        driver.removeSummoningSickness(spider)
        driver.untapPermanent(spider)
        return spider
    }

    test("has reach") {
        SpiderManifestation.keywords.contains(Keyword.REACH) shouldBe true
    }

    test("the dual mana line is two abilities, not one colour choice") {
        manaAbilities.size shouldBe 2
    }

    test("each mana ability taps for its own colour without asking for a choice") {
        val expected = listOf(Color.RED, Color.GREEN)
        for ((index, color) in expected.withIndex()) {
            val driver = setup()
            val p1 = driver.activePlayer!!
            val spider = readySpider(driver, p1)
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            driver.submitSuccess(
                ActivateAbility(playerId = p1, sourceId = spider, abilityId = manaAbilities[index].id)
            )

            // A two-ability dual resolves straight into the pool — nothing left to decide.
            driver.pendingDecision.shouldBeNull()
            driver.isTapped(spider) shouldBe true

            val manaPool = pool(driver, p1)
            manaPool.red shouldBe if (color == Color.RED) 1 else 0
            manaPool.green shouldBe if (color == Color.GREEN) 1 else 0
        }
    }

    test("casting a spell with mana value 4 or greater untaps it") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val spider = readySpider(driver, p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.tapPermanent(spider)

        // Force of Nature is {3}{G}{G} — mana value 5.
        val bigSpell = driver.putCardInHand(p1, "Force of Nature")
        driver.giveMana(p1, Color.GREEN, 2)
        driver.giveColorlessMana(p1, 3)
        driver.castSpell(p1, bigSpell).error shouldBe null

        // The trigger sits above the spell; resolving it untaps the Spider.
        driver.bothPass()
        driver.isTapped(spider) shouldBe false
    }

    test("casting a spell with mana value 3 does not untap it") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val spider = readySpider(driver, p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.tapPermanent(spider)

        // Centaur Courser is {2}{G} — mana value 3, one under the threshold.
        val smallSpell = driver.putCardInHand(p1, "Centaur Courser")
        driver.giveMana(p1, Color.GREEN, 1)
        driver.giveColorlessMana(p1, 2)
        driver.castSpell(p1, smallSpell).error shouldBe null

        driver.bothPass()
        driver.isTapped(spider) shouldBe true
    }
})
