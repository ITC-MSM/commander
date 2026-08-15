package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Rip, Spawn Hunter (DSK #228) — {2}{G}{W} 4/4 Legendary Creature — Human Survivor.
 *
 * "Survival — At the beginning of your second main phase, if Rip is tapped, reveal the top X
 *  cards of your library, where X is its power. Put any number of creature and/or Vehicle cards
 *  with different powers from among them into your hand. Put the rest on the bottom of your
 *  library in a random order."
 *
 * Exercises the `SelectionRestriction.OnePerPower` capability end-to-end through the
 * reveal → select → bottom pipeline:
 *  - X equals the source's power (reveals exactly that many cards);
 *  - the controller keeps any number of creature/Vehicle cards with *different* powers;
 *  - a second card sharing an already-chosen power is rejected server-side and bottoms instead;
 *  - non-creature/non-Vehicle revealed cards and unpicked cards go to the bottom of the library;
 *  - the intervening-if (Rip tapped) gates the whole trigger.
 */
class RipSpawnHunterScenarioTest : ScenarioTestBase() {

    private val p1 = EntityId.of("player-1")

    private fun testCreature(name: String, power: Int): CardDefinition =
        CardDefinition.creature(
            name = name,
            manaCost = ManaCost.parse("{2}"),
            subtypes = setOf(Subtype("Beast")),
            power = power,
            toughness = power,
        )

    /** A Vehicle: a non-creature artifact carrying the Vehicle subtype, with printed power. */
    private fun testVehicle(name: String, power: Int): CardDefinition =
        CardDefinition(
            name = name,
            manaCost = ManaCost.parse("{3}"),
            typeLine = TypeLine(cardTypes = setOf(CardType.ARTIFACT), subtypes = setOf(Subtype.VEHICLE)),
            creatureStats = CreatureStats(power, power),
        )

    init {
        // Inline test cards with deterministic printed powers, including a Vehicle.
        cardRegistry.register(
            listOf(
                testCreature("Spawn Two A", 2),
                testCreature("Spawn Two B", 2),
                testCreature("Spawn Three", 3),
                testCreature("Spawn Six", 6),
                testCreature("Spawn Seven", 7),
                testVehicle("Hunt Rig", 5),
            )
        )

        fun libraryNames(game: TestGame): List<String> =
            game.state.getZone(ZoneKey(p1, com.wingedsheep.sdk.core.Zone.LIBRARY))
                .mapNotNull { game.state.getEntity(it)?.get<CardComponent>()?.name }

        fun handNames(game: TestGame): List<String> =
            game.state.getHand(p1)
                .mapNotNull { game.state.getEntity(it)?.get<CardComponent>()?.name }

        fun optionFor(game: TestGame, decision: SelectCardsDecision, name: String): EntityId =
            decision.options.first { game.state.getEntity(it)?.get<CardComponent>()?.name == name }

        fun resolveToSelection(game: TestGame): SelectCardsDecision {
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
            var guard = 0
            while (game.getPendingDecision() !is SelectCardsDecision && guard++ < 20) {
                game.resolveStack()
            }
            return game.getPendingDecision() as? SelectCardsDecision
                ?: error("expected a SelectCardsDecision; got ${game.getPendingDecision()}")
        }

        context("Survival — reveal top X (= power), keep distinct-power creatures/Vehicles") {

            test("reveals exactly X cards where X is Rip's power") {
                // Library top→bottom: 5 creatures/Vehicles; Rip's power is 4, so only the top 4
                // are revealed and offered — the fifth (Spawn Seven) is never seen.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Rip, Spawn Hunter", tapped = true, summoningSickness = false)
                    .withCardInLibrary(1, "Spawn Two A")   // power 2
                    .withCardInLibrary(1, "Spawn Three")   // power 3
                    .withCardInLibrary(1, "Hunt Rig")      // Vehicle, power 5
                    .withCardInLibrary(1, "Spawn Six")     // power 6
                    .withCardInLibrary(1, "Spawn Seven")   // power 7 — below the top 4
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val decision = resolveToSelection(game)

                withClue("X = Rip's power (4): exactly the top four cards are offered") {
                    decision.options.size shouldBe 4
                }

                // All four have different powers (2, 3, 5, 6) — keep them all.
                game.selectCards(
                    listOf(
                        optionFor(game, decision, "Spawn Two A"),
                        optionFor(game, decision, "Spawn Three"),
                        optionFor(game, decision, "Hunt Rig"),
                        optionFor(game, decision, "Spawn Six"),
                    )
                )
                game.resolveStack()

                withClue("the four distinct-power cards went to hand") {
                    handNames(game) shouldContainExactlyInAnyOrder
                        listOf("Spawn Two A", "Spawn Three", "Hunt Rig", "Spawn Six")
                }
                withClue("the un-revealed fifth card stayed in the library") {
                    libraryNames(game) shouldBe listOf("Spawn Seven")
                }
            }

            test("a non-creature/non-Vehicle card in the reveal goes to the bottom") {
                // Top 4: three creature/Vehicle cards + a Forest. Keep the three; the Forest bottoms.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Rip, Spawn Hunter", tapped = true, summoningSickness = false)
                    .withCardInLibrary(1, "Spawn Two A")
                    .withCardInLibrary(1, "Spawn Three")
                    .withCardInLibrary(1, "Hunt Rig")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val decision = resolveToSelection(game)

                withClue("the land is not an eligible (selectable) option") {
                    decision.options.size shouldBe 3
                }

                game.selectCards(
                    listOf(
                        optionFor(game, decision, "Spawn Two A"),
                        optionFor(game, decision, "Spawn Three"),
                        optionFor(game, decision, "Hunt Rig"),
                    )
                )
                game.resolveStack()

                withClue("creatures/Vehicle into hand") {
                    handNames(game) shouldContainExactlyInAnyOrder
                        listOf("Spawn Two A", "Spawn Three", "Hunt Rig")
                }
                withClue("the rest (the Forest) went to the bottom of the library") {
                    libraryNames(game) shouldBe listOf("Forest")
                }
            }
        }

        context("Survival — different-powers restriction (OnePerPower)") {

            test("a second card sharing an already-chosen power is rejected and bottoms instead") {
                // Top 4: two power-2 creatures, a power-3 creature, a power-5 Vehicle.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Rip, Spawn Hunter", tapped = true, summoningSickness = false)
                    .withCardInLibrary(1, "Spawn Two A")
                    .withCardInLibrary(1, "Spawn Two B")
                    .withCardInLibrary(1, "Spawn Three")
                    .withCardInLibrary(1, "Hunt Rig")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val decision = resolveToSelection(game)

                // Try to keep both power-2 creatures plus the power-3 creature. The second power-2
                // pick is rejected (different powers required); Hunt Rig was never selected.
                game.selectCards(
                    listOf(
                        optionFor(game, decision, "Spawn Two A"),
                        optionFor(game, decision, "Spawn Two B"),
                        optionFor(game, decision, "Spawn Three"),
                    )
                )
                game.resolveStack()

                withClue("only the first power-2 card and the power-3 card reached hand") {
                    handNames(game) shouldContainExactlyInAnyOrder listOf("Spawn Two A", "Spawn Three")
                }
                withClue("the duplicate-power card was rejected and is no longer in hand") {
                    handNames(game) shouldNotContain "Spawn Two B"
                }
                withClue("the rejected duplicate and the un-picked Vehicle bottomed out") {
                    libraryNames(game) shouldContainExactlyInAnyOrder listOf("Spawn Two B", "Hunt Rig")
                }
            }
        }

        context("Survival — gating and declining") {

            test("selecting nothing bottoms every revealed card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Rip, Spawn Hunter", tapped = true, summoningSickness = false)
                    .withCardInLibrary(1, "Spawn Two A")
                    .withCardInLibrary(1, "Spawn Three")
                    .withCardInLibrary(1, "Hunt Rig")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                resolveToSelection(game)
                game.skipSelection()
                game.resolveStack()

                withClue("no card entered hand") {
                    handNames(game).any { it in setOf("Spawn Two A", "Spawn Three", "Hunt Rig") } shouldBe false
                }
                withClue("every revealed card is back on the bottom of the library") {
                    libraryNames(game) shouldContainExactlyInAnyOrder
                        listOf("Spawn Two A", "Spawn Three", "Hunt Rig", "Forest")
                }
            }

            test("an untapped Rip does not trigger Survival") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Rip, Spawn Hunter", tapped = false, summoningSickness = false)
                    .withCardInLibrary(1, "Spawn Two A")
                    .withCardInLibrary(1, "Spawn Three")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                repeat(5) { if (!game.hasPendingDecision()) game.resolveStack() }

                withClue("intervening-if (Rip is tapped) is false — no decision, library intact") {
                    game.hasPendingDecision() shouldBe false
                    libraryNames(game) shouldBe listOf("Spawn Two A", "Spawn Three")
                }
            }
        }
    }
}
