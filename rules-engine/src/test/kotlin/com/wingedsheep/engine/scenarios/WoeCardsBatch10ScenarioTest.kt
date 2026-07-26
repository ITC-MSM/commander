package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for the tenth batch of Wilds of Eldraine cards.
 *
 * The batch adds one piece of SDK vocabulary —
 * [StatePredicate.IsEnchanted][com.wingedsheep.sdk.scripting.predicates.StatePredicate.IsEnchanted],
 * "has an Aura attached" — so the bulk of this file pins that predicate down from both of its two
 * users and from both of the engine paths it resolves on:
 *
 *  - **A Tale for the Ages** exercises it in layer-7c *group projection* (`AffectsFilterResolver`).
 *    The traps a plausible implementation falls into are all encoded here: an Equipment-only creature
 *    must **not** be buffed (the predicate is not `IsModified`), an opponent's creature must not be
 *    buffed even while carrying your own Aura, and your creature carrying an *opponent's* Aura must
 *    be (CR 303.4 — the Aura's controller is irrelevant). The buff also has to appear and disappear
 *    as Auras arrive and leave, which is what makes it a projection concern rather than a one-shot.
 *  - **Lord Skitter's Blessing** exercises it as a *trigger gate* (`PredicateEvaluator`), which is a
 *    separate code path. Its intervening-'if' (CR 603.4) must suppress the whole ability — no life
 *    loss, no extra card — when nothing you control is enchanted.
 *
 * The remaining three cards are pure compositions, but each has one place a plausible model resolves
 * wrong, so they get a check apiece: **Stonesplitter Bolt**'s "twice X" (both branches, and X = 0),
 * **Tenacious Tomeseeker**'s bargain-gated enters trigger, and **Howling Galefang**'s conditional
 * haste.
 */
class WoeCardsBatch10ScenarioTest : ScenarioTestBase() {

    private fun power(game: TestGame, id: EntityId): Int? = game.state.projectedState.getPower(id)
    private fun toughness(game: TestGame, id: EntityId): Int? = game.state.projectedState.getToughness(id)

    private fun auraOn(game: TestGame, auraName: String, host: EntityId): EntityId? =
        game.findPermanents(auraName).firstOrNull { aura ->
            game.state.getEntity(aura)?.get<AttachedToComponent>()?.targetId == host
        }

    init {
        context("A Tale for the Ages — 'enchanted creatures you control get +2/+2'") {
            test("buffs your enchanted creature, and leaves your unenchanted one alone") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "A Tale for the Ages")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Redtooth Genealogist")
                    .withCardAttachedTo(1, "Pacifism", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                val elf = game.findPermanent("Redtooth Genealogist").shouldNotBeNull()

                withClue("the enchanted 2/2 Bears projects as 4/4") {
                    power(game, bears) shouldBe 4
                    toughness(game, bears) shouldBe 4
                }
                withClue("the unenchanted 2/3 Genealogist is untouched") {
                    power(game, elf) shouldBe 2
                    toughness(game, elf) shouldBe 3
                }
            }

            test("an Equipment attached is not 'enchanted' — the predicate is not IsModified") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "A Tale for the Ages")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Whispersilk Cloak", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                withClue("Whispersilk Cloak is Equipment, not an Aura — the Bears stays 2/2") {
                    power(game, bears) shouldBe 2
                    toughness(game, bears) shouldBe 2
                }
            }

            test("your creature enchanted by an opponent's Aura still gets the buff (CR 303.4)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "A Tale for the Ages")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    // Player 2 owns and controls the Pacifism; player 1 controls the creature.
                    .withCardAttachedTo(2, "Pacifism", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                withClue("'enchanted' asks only that an Aura be attached, whoever controls it") {
                    power(game, bears) shouldBe 4
                    toughness(game, bears) shouldBe 4
                }
            }

            test("an opponent's enchanted creature is not buffed — 'you control' scopes the creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "A Tale for the Ages")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    // Player 1's own Aura, on player 2's creature.
                    .withCardAttachedTo(1, "Pacifism", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                withClue("the creature is the opponent's, so the anthem does not see it") {
                    power(game, bears) shouldBe 2
                    toughness(game, bears) shouldBe 2
                }
            }

            test("the buff falls away when the Aura leaves — it is projected, not applied once") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "A Tale for the Ages")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Pacifism", "Grizzly Bears")
                    .withCardInHand(1, "Stonesplitter Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                power(game, bears) shouldBe 4

                // Destroy the Aura by bouncing it off: kill the host with X=4 damage, which takes
                // the Aura to the graveyard with it (CR 704.5m) and removes the anthem's subject.
                game.castXSpell(1, "Stonesplitter Bolt", xValue = 4, targetId = bears)
                    .error shouldBe null
                game.resolveStack()

                withClue("a 4/4 enchanted Bears dies to 4 damage, and its Aura follows it") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(1, "Pacifism") shouldBe true
                }
            }
        }

        context("Lord Skitter's Blessing") {
            test("the enters trigger attaches a Wicked Role to the targeted creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lord Skitter's Blessing")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                game.castSpell(1, "Lord Skitter's Blessing").error shouldBe null
                game.resolveStack()

                // The Role's target belongs to the *enters* trigger, so it is chosen when that
                // trigger goes on the stack — after the enchantment itself has resolved.
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                withClue("a Wicked Role token is attached to the Bears") {
                    auraOn(game, "Wicked Role", bears).shouldNotBeNull()
                }
                withClue("Wicked Role grants +1/+1 — the 2/2 Bears projects as 3/3") {
                    power(game, bears) shouldBe 3
                    toughness(game, bears) shouldBe 3
                }
            }

            test("the draw step fires when you control an enchanted creature: -1 life, +1 card") {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Lord Skitter's Blessing")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Pacifism", "Grizzly Bears")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    // Not turn 1 — the starting player skips their first draw step (CR 103.7a),
                    // which would hide the turn-based draw this test counts against.
                    .withTurnNumber(3)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                repeat(10) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(10) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val handBefore = game.handSize(1)

                game.passUntilPhase(Phase.BEGINNING, Step.DRAW)
                game.resolveStack()

                withClue("you lose 1 life") { game.getLifeTotal(1) shouldBe 19 }
                withClue("the turn-based draw plus the additional card — two cards this draw step") {
                    game.handSize(1) shouldBe handBefore + 2
                }
            }

            test("with nothing enchanted the intervening 'if' suppresses the whole ability") {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Lord Skitter's Blessing")
                    // A creature, but no Aura on it.
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .withTurnNumber(3)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                repeat(10) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(10) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val handBefore = game.handSize(1)

                game.passUntilPhase(Phase.BEGINNING, Step.DRAW)
                game.resolveStack()

                withClue("no life lost — the ability never went on the stack") {
                    game.getLifeTotal(1) shouldBe 20
                }
                withClue("only the turn-based draw") { game.handSize(1) shouldBe handBefore + 1 }
            }
        }

        context("Stonesplitter Bolt — X damage, twice X if bargained") {
            test("unbargained deals exactly X") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Stonesplitter Bolt")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                // X = 1 into a 2/2: survives. Were the amount doubled without bargain, it would die.
                game.castXSpell(1, "Stonesplitter Bolt", xValue = 1, targetId = bears)
                    .error shouldBe null
                game.resolveStack()

                withClue("1 damage does not kill a 2/2") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }

            test("bargained deals twice X — the same X that survived unbargained now kills") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Stonesplitter Bolt")
                    .withCardOnBattlefield(1, "A Tale for the Ages")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                // Sacrifice the enchantment to bargain; X = 1 becomes 2 damage, lethal to the 2/2.
                game.castSpellBargained(
                    1,
                    "Stonesplitter Bolt",
                    sacrificeName = "A Tale for the Ages",
                    targetId = bears,
                    xValue = 1,
                ).error shouldBe null
                game.resolveStack()

                withClue("the enchantment paid the bargain cost") {
                    game.isInGraveyard(1, "A Tale for the Ages") shouldBe true
                }
                withClue("twice X = 2 damage kills the 2/2") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
            }

            test("X = 0 bargained is still 0 damage — 'twice X', not a flat doubling") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Stonesplitter Bolt")
                    .withCardOnBattlefield(1, "A Tale for the Ages")
                    .withCardOnBattlefield(2, "Savannah Lions")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val lions = game.findPermanent("Savannah Lions").shouldNotBeNull()

                game.castSpellBargained(
                    1,
                    "Stonesplitter Bolt",
                    sacrificeName = "A Tale for the Ages",
                    targetId = lions,
                    xValue = 0,
                ).error shouldBe null
                game.resolveStack()

                withClue("twice 0 is 0 — the 2/1 lives") {
                    game.isOnBattlefield("Savannah Lions") shouldBe true
                }
            }
        }

        context("Tenacious Tomeseeker — bargain-gated graveyard return") {
            test("bargained, it returns the targeted instant from your graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Tenacious Tomeseeker")
                    .withCardOnBattlefield(1, "A Tale for the Ages")
                    .withCardInGraveyard(1, "Candy Grapple")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellBargained(
                    1,
                    "Tenacious Tomeseeker",
                    sacrificeName = "A Tale for the Ages",
                ).error shouldBe null
                game.resolveStack()

                // The graveyard target belongs to the enters trigger, chosen once that trigger is
                // put on the stack — i.e. only on the bargained branch (CR 702.166d).
                val grapple = game.findCardsInGraveyard(1, "Candy Grapple").single()
                game.selectTargets(listOf(grapple)).error shouldBe null
                game.resolveStack()

                withClue("the bargained enters trigger returned the instant to hand") {
                    game.isInHand(1, "Candy Grapple") shouldBe true
                    game.isInGraveyard(1, "Candy Grapple") shouldBe false
                }
            }

            test("unbargained, the enters trigger never goes on the stack (CR 603.4)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Tenacious Tomeseeker")
                    .withCardInGraveyard(1, "Candy Grapple")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Tenacious Tomeseeker").error shouldBe null
                game.resolveStack()

                withClue("the creature resolved") {
                    game.isOnBattlefield("Tenacious Tomeseeker") shouldBe true
                }
                withClue("no bargain, so nothing came back and no decision was raised") {
                    game.isInGraveyard(1, "Candy Grapple") shouldBe true
                    game.hasPendingDecision() shouldBe false
                }
            }
        }

        context("Howling Galefang — haste while you own an Adventure card in exile") {
            test("no Adventure card in exile: vigilance only, no haste") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Howling Galefang")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val fang = game.findPermanent("Howling Galefang").shouldNotBeNull()
                val projected = game.state.projectedState

                projected.hasKeyword(fang, Keyword.VIGILANCE) shouldBe true
                withClue("nothing in exile, so the conditional grant is off") {
                    projected.hasKeyword(fang, Keyword.HASTE) shouldBe false
                }
            }

            test("an Adventure card you own in exile turns haste on, however it got there") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    // Besotted Knight // Betroth the Beast — an Adventure card. Per the WOE ruling
                    // it does not matter that it was not cast as an Adventure.
                    .withCardInExile(1, "Besotted Knight")
                    .withCardOnBattlefield(1, "Howling Galefang")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val fang = game.findPermanent("Howling Galefang").shouldNotBeNull()

                withClue("a card with an Adventure sits in your exile zone") {
                    game.state.projectedState.hasKeyword(fang, Keyword.HASTE) shouldBe true
                }
            }

            test("an Adventure card in an opponent's exile does not grant it — 'you own'") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInExile(2, "Besotted Knight")
                    .withCardOnBattlefield(1, "Howling Galefang")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val fang = game.findPermanent("Howling Galefang").shouldNotBeNull()

                withClue("the exiled Adventure is the opponent's, so no haste") {
                    game.state.projectedState.hasKeyword(fang, Keyword.HASTE) shouldBe false
                }
            }

            test("a non-Adventure card in your exile does not grant haste") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInExile(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Howling Galefang")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val fang = game.findPermanent("Howling Galefang").shouldNotBeNull()

                withClue("Grizzly Bears has no Adventure") {
                    game.state.projectedState.hasKeyword(fang, Keyword.HASTE) shouldBe false
                }
            }
        }
    }
}
