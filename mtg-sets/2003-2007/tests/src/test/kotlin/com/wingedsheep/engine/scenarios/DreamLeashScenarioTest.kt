package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Dream Leash (RAV #45) — "Enchant permanent / You can't choose an untapped permanent as this
 * spell's target as you cast it. / You control enchanted permanent."
 *
 * The tapped requirement is a cast-time-only narrowing (`auraCastTarget`): it limits the choice,
 * but — per the 2005-10-01 rulings — it is not re-checked when the spell resolves, and the Aura
 * does not fall off when the permanent untaps later.
 */
class DreamLeashScenarioTest : ScenarioTestBase() {

    private fun board() = scenario()
        .withPlayers("Alice", "Bob")
        .withCardInHand(1, "Dream Leash")
        .withLandsOnBattlefield(1, "Island", 5)
        .withCardOnBattlefield(2, "Grizzly Bears")
        .withCardOnBattlefield(2, "Hill Giant", tapped = true)
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

    init {
        context("Dream Leash") {

            test("only tapped permanents are offered as the cast-time target") {
                val game = board().build()
                val giant = game.findPermanent("Hill Giant")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                val cast = game.getLegalActions(1).single {
                    (it.action as? CastSpell)?.cardId == game.findCardsInHand(1, "Dream Leash").single()
                }
                val offered = cast.validTargets.orEmpty()
                withClue("the tapped Giant is a legal choice") { offered.contains(giant) shouldBe true }
                withClue("the untapped Bears are not") { offered.contains(bears) shouldBe false }
                withClue("untapped lands aren't either") {
                    game.findAllPermanents("Island").none { it in offered } shouldBe true
                }
            }

            test("casting it at an untapped permanent is rejected") {
                val game = board().build()
                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Dream Leash", bears).error shouldNotBe null
                game.isInHand(1, "Dream Leash") shouldBe true
            }

            test("with no tapped permanent it can't be cast at all") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Dream Leash")
                    .withLandsOnBattlefield(1, "Island", 5)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()
                val leash = game.findCardsInHand(1, "Dream Leash").single()
                game.getLegalActions(1).none { (it.action as? CastSpell)?.cardId == leash } shouldBe true
            }

            test("the target untapping before resolution doesn't counter it, and untapping later doesn't drop it") {
                val game = board().build()
                val giant = game.findPermanent("Hill Giant")!!

                game.castSpell(1, "Dream Leash", giant).error shouldBe null

                // The Giant untaps while Dream Leash is on the stack.
                game.state = game.state.updateEntity(giant) { it.without<TappedComponent>() }
                game.resolveStack()

                withClue("the tapped requirement is not re-checked on resolution") {
                    game.isOnBattlefield("Dream Leash") shouldBe true
                    val leash = game.findPermanent("Dream Leash")!!
                    game.state.getEntity(leash)!!.get<AttachedToComponent>()!!.targetId shouldBe giant
                }
                withClue("Alice controls the enchanted permanent") {
                    game.state.projectedState.getController(giant) shouldBe game.player1Id
                }

                game.checkStateBasedActions()
                withClue("an untapped enchanted permanent is still a legal host") {
                    game.isOnBattlefield("Dream Leash") shouldBe true
                    game.state.projectedState.getController(giant) shouldBe game.player1Id
                }
            }
        }
    }
}
