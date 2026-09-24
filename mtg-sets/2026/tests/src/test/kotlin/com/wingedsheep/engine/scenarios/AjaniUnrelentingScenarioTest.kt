package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AbilityCost
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * Ajani Unrelenting (FRA #242).
 *
 * - Whenever you activate a loyalty ability, create a 2/2 colorless Wizard Soldier creature token
 *   named Cadet.
 * - +1: Creatures you control get +1/+0 and gain haste until end of turn.
 * - −2: Discard your hand, then draw a card for each creature you control.
 * - −3: Ajani deals 4 damage to each creature except for tokens you control.
 */
class AjaniUnrelentingScenarioTest : ScenarioTestBase() {
    init {
        val abilities = cardRegistry.getCard("Ajani Unrelenting")!!.script.activatedAbilities
        fun loyalty(change: Int) = abilities.single { (it.cost as? AbilityCost.Loyalty)?.change == change }.id

        fun base() = scenario().withPlayers()
            .withCardOnBattlefield(1, "Ajani Unrelenting")
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(2, "Swamp")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("+1 makes a Cadet first, then pumps and hastes every creature you control") {
            val game = base()
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = true)
                .withCardOnBattlefield(2, "Hill Giant")
                .build()
            val ajani = game.findPermanent("Ajani Unrelenting")!!

            game.execute(ActivateAbility(game.player1Id, ajani, loyalty(1))).error shouldBe null
            game.resolveStack()

            val cadet = game.findPermanent("Cadet")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            val giant = game.findPermanent("Hill Giant")!!
            game.state.projectedState.getPower(cadet) shouldBe 3
            game.state.projectedState.getToughness(cadet) shouldBe 2
            game.state.projectedState.hasKeyword(cadet, Keyword.HASTE) shouldBe true
            game.state.projectedState.getPower(bears) shouldBe 3
            game.state.projectedState.hasKeyword(bears, Keyword.HASTE) shouldBe true
            withClue("opponents' creatures are untouched") {
                game.state.projectedState.getPower(giant) shouldBe 3
                game.state.projectedState.hasKeyword(giant, Keyword.HASTE) shouldBe false
            }
            game.state.projectedState.getColors(cadet) shouldBe emptySet()
            game.state.projectedState.hasSubtype(cadet, "Wizard") shouldBe true
            game.state.projectedState.hasSubtype(cadet, "Soldier") shouldBe true
        }

        test("another planeswalker's loyalty ability also makes a Cadet") {
            val game = base()
                .withCardOnBattlefield(1, "Garruk, Veiled Butcher")
                .build()
            val garruk = game.findPermanent("Garruk, Veiled Butcher")!!
            val garrukPlusTwo = cardRegistry.getCard("Garruk, Veiled Butcher")!!.script.activatedAbilities
                .single { (it.cost as? AbilityCost.Loyalty)?.change == 2 }.id

            game.execute(ActivateAbility(game.player1Id, garruk, garrukPlusTwo)).error shouldBe null
            game.resolveStack()

            game.findPermanents("Cadet") shouldHaveSize 1
        }

        test("−2 discards your hand, then draws one per creature you control (Cadet included)") {
            val game = base()
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .withCardInHand(1, "Lightning Bolt")
                .withCardInHand(1, "Lightning Bolt")
                .withCardInHand(1, "Lightning Bolt")
                .build()
            val ajani = game.findPermanent("Ajani Unrelenting")!!

            game.execute(ActivateAbility(game.player1Id, ajani, loyalty(-2))).error shouldBe null
            game.resolveStack()

            game.findCardsInGraveyard(1, "Lightning Bolt") shouldHaveSize 3
            withClue("Grizzly Bears + the Cadet = two cards drawn") {
                game.handSize(1) shouldBe 2
            }
        }

        test("−3 deals 4 to each creature except tokens you control") {
            val game = base()
                .withCardOnBattlefield(1, "Hill Giant")
                .withCardOnBattlefield(1, "Grizzly Bears", isToken = true)
                .withCardOnBattlefield(2, "Savannah Lions", isToken = true)
                .withCardOnBattlefield(2, "Craw Wurm")
                .build()
            val ajani = game.findPermanent("Ajani Unrelenting")!!

            game.execute(ActivateAbility(game.player1Id, ajani, loyalty(-3))).error shouldBe null
            game.resolveStack()

            withClue("your nontoken creature is hit") {
                game.isOnBattlefield("Hill Giant") shouldBe false
            }
            withClue("your tokens are spared — including the fresh Cadet") {
                game.isOnBattlefield("Grizzly Bears") shouldBe true
                game.isOnBattlefield("Cadet") shouldBe true
            }
            withClue("an opponent's token is hit") {
                game.isOnBattlefield("Savannah Lions") shouldBe false
            }
            withClue("the 6/4 Craw Wurm dies to 4 damage") {
                game.isOnBattlefield("Craw Wurm") shouldBe false
            }
            game.isOnBattlefield("Ajani Unrelenting") shouldBe true
        }
    }
}
