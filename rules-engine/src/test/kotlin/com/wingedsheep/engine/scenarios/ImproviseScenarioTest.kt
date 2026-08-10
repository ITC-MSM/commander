package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.view.LegalActionInfo
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.AlternativePaymentChoice
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Engine-level scenario tests for **improvise** (CR 702.126) — the mechanic, not one card.
 *
 * CR 702.126a: *"For each generic mana in this spell's total cost, you may tap an untapped
 * artifact you control rather than pay that mana."* CR 702.126b adds that improvise is neither an
 * additional nor an alternative cost and applies only after the total cost is determined, and
 * CR 702.126c that multiple instances are redundant.
 *
 * Improvise is the artifacts-only case of the shared tap-for-generic payment rail: the chosen
 * artifacts travel in [AlternativePaymentChoice.tapForGenericPermanents], the same carrier a
 * waterbend cost uses, and the eligibility filter is what separates them.
 *
 * Rules pinned here:
 *  1. Each tapped artifact pays {1} generic, and the artifacts end up tapped.
 *  2. Improvise never pays a colored pip — the colored part still needs real mana.
 *  3. Taps beyond the generic in the cost are ignored (and those artifacts stay untapped).
 *  4. Only untapped artifacts the caster controls are eligible — not creatures, not the
 *     opponent's, not already-tapped ones.
 *  5. The cast is *enumerated* as affordable when the artifacts make up the shortfall, and the
 *     legal action carries the tap metadata and the "improvise" label the client renders.
 *  6. A spell without improvise ignores the taps entirely — nothing is tapped, nothing is
 *     discounted, and the cast fails for lack of mana.
 */
class ImproviseScenarioTest : ScenarioTestBase() {

    init {
        // {4}{U} sorcery with printed improvise: one colored pip, four generic.
        val improviser = card("Improvising Blueprint") {
            manaCost = "{4}{U}"
            colorIdentity = "U"
            typeLine = "Sorcery"
            oracleText = "Improvise\nYou gain 5 life."
            keywords(Keyword.IMPROVISE)
            spell {
                effect = Effects.GainLife(5)
            }
        }
        cardRegistry.register(improviser)

        // Same cost, no improvise — the control for rule 6.
        val plain = card("Plain Blueprint") {
            manaCost = "{4}{U}"
            colorIdentity = "U"
            typeLine = "Sorcery"
            oracleText = "You gain 5 life."
            spell {
                effect = Effects.GainLife(5)
            }
        }
        cardRegistry.register(plain)

        val trinket = card("Improvise Trinket") {
            manaCost = "{1}"
            colorIdentity = ""
            typeLine = "Artifact"
            oracleText = ""
        }
        cardRegistry.register(trinket)

        fun castAction(game: TestGame, name: String): LegalActionInfo? =
            game.getLegalActions(1).firstOrNull {
                it.actionType == "CastSpell" && it.action is CastSpell && it.description.contains(name)
            }

        context("Paying with improvise") {

            test("each tapped artifact pays {1} of the generic, and the artifacts are tapped") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Improvising Blueprint")
                    .withLandsOnBattlefield(1, "Island", 1) // only the {U}
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val before = game.getLifeTotal(1)
                val artifacts = game.findAllPermanents("Improvise Trinket")
                artifacts.size shouldBe 4

                val action = castAction(game, "Improvising Blueprint")
                withClue("one Island plus four artifacts should make {4}{U} affordable") {
                    action shouldNotBe null
                    action!!.isAffordable shouldBe true
                }

                val cast = (action!!.action as CastSpell).copy(
                    alternativePayment = AlternativePaymentChoice(tapForGenericPermanents = artifacts.toSet())
                )
                val result = game.execute(cast)
                withClue("four artifacts should cover the {4}: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
                game.getLifeTotal(1) shouldBe before + 5
                withClue("every improvised artifact ends up tapped") {
                    artifacts.all { game.state.getEntity(it)!!.has<TappedComponent>() } shouldBe true
                }
            }

            test("improvise cannot pay the colored pip") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Improvising Blueprint")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Five artifacts, no lands: CR 702.126a only lets them pay the four generic, so the
                // {U} is still owed and the cast must not be offered as affordable.
                val action = castAction(game, "Improvising Blueprint")
                withClue("with no blue source the improvise spell is not castable") {
                    (action == null || !action.isAffordable) shouldBe true
                }
            }

            test("taps beyond the generic in the cost are ignored and stay untapped") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Improvising Blueprint")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val artifacts = game.findAllPermanents("Improvise Trinket")
                artifacts.size shouldBe 6
                val action = castAction(game, "Improvising Blueprint")!!
                val cast = (action.action as CastSpell).copy(
                    alternativePayment = AlternativePaymentChoice(tapForGenericPermanents = artifacts.toSet())
                )
                val result = game.execute(cast)
                result.error shouldBe null
                game.resolveStack()

                val tapped = artifacts.count { game.state.getEntity(it)!!.has<TappedComponent>() }
                withClue("only the {4} of generic can be improvised — the other two artifacts stay untapped") {
                    tapped shouldBe 4
                }
            }
        }

        context("Eligibility") {

            test("only the caster's untapped artifacts are offered") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Improvising Blueprint")
                    .withLandsOnBattlefield(1, "Island", 5)
                    .withCardOnBattlefield(1, "Improvise Trinket")                 // eligible
                    .withCardOnBattlefield(1, "Improvise Trinket", tapped = true)  // already tapped
                    .withCardOnBattlefield(1, "Glory Seeker")                      // creature, not an artifact
                    .withCardOnBattlefield(2, "Improvise Trinket")                 // opponent's
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val action = castAction(game, "Improvising Blueprint")
                withClue("the cast should carry the tap-for-generic metadata") {
                    action shouldNotBe null
                    action!!.hasTapForGeneric shouldBe true
                    action.tapForGenericLabel shouldBe "improvise"
                    withClue("no cap beyond the generic in the cost (CR 702.126a)") {
                        action.tapForGenericAmount shouldBe null
                    }
                }

                val mine = game.findAllPermanents("Improvise Trinket").filter { id ->
                    val entity = game.state.getEntity(id)!!
                    entity.get<ControllerComponent>()?.playerId == game.player1Id &&
                        !entity.has<TappedComponent>()
                }
                mine.size shouldBe 1
                val offered = action!!.validTapForGenericPermanents!!.map { it.entityId }
                withClue("creatures, tapped artifacts and the opponent's artifacts are all excluded") {
                    offered shouldContainExactlyInAnyOrder mine
                }
            }

            test("a spell without improvise ignores the taps entirely") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Plain Blueprint")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withCardOnBattlefield(1, "Improvise Trinket")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val action = castAction(game, "Plain Blueprint")
                withClue("without improvise, one Island cannot pay {4}{U}") {
                    (action == null || !action.isAffordable) shouldBe true
                }

                // Force the action through anyway: a forged tap payment must not discount the cost
                // or tap anything.
                val cardId = game.state.getZone(ZoneKey(game.player1Id, Zone.HAND)).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Plain Blueprint"
                }
                val artifacts = game.findAllPermanents("Improvise Trinket")
                val result = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        alternativePayment = AlternativePaymentChoice(tapForGenericPermanents = artifacts.toSet())
                    )
                )
                withClue("the cast must fail — improvise isn't there to be used") {
                    result.error shouldNotBe null
                }
                withClue("and nothing was tapped on the way to failing") {
                    artifacts.none { result.state.getEntity(it)!!.has<TappedComponent>() } shouldBe true
                }
            }
        }
    }
}
