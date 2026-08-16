package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.TappedEvent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Captain America, Living Legend (MSH #210) — {1}{W}{U} 3/4 Legendary Human Soldier Hero.
 *
 * "Vigilance. Whenever a creature you control becomes tapped during your turn, if it's the first
 * time that creature has become tapped this turn, untap it."
 *
 * Three riders, one test each way round: the "creature you control" filter, the "during your turn"
 * gate, and the per-permanent first-time window (the primitive this card motivated — its own axis
 * tests live in `FirstTimeTappedThisTurnScenarioTest`).
 *
 * Every negative case asserts the creature is *still tapped* after the stack has been drained, and
 * the positive cases assert it is untapped, so neither direction can pass by accident.
 */
class CaptainAmericaLivingLegendScenarioTest : ScenarioTestBase() {

    /** "Tap target creature." A tap handle either player can point anywhere. */
    private val tapPulse = card("Tap Pulse") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "Tap target creature."
        spell {
            val t = target("target", TargetCreature(filter = TargetFilter.Creature))
            effect = Effects.Tap(t)
        }
    }

    /** "Untap target creature." Needed to re-tap the same creature inside one turn. */
    private val untapPulse = card("Untap Pulse") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "Untap target creature."
        spell {
            val t = target("target", TargetCreature(filter = TargetFilter.Creature))
            effect = Effects.Untap(t)
        }
    }

    /** "Regenerate target creature." Banks the shield without needing a creature that has one. */
    private val shieldUp = card("Shield Up") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "Regenerate target creature."
        spell {
            val t = target("target", TargetCreature(filter = TargetFilter.Creature))
            effect = RegenerateEffect(t)
        }
    }

    /** "Deals 3 damage to target creature." The destruction that regeneration replaces. */
    private val boltPulse = card("Bolt Pulse") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "Bolt Pulse deals 3 damage to target creature."
        spell {
            val t = target("target", TargetCreature(filter = TargetFilter.Creature))
            effect = Effects.DealDamage(3, t)
        }
    }

    init {
        cardRegistry.register(tapPulse)
        cardRegistry.register(untapPulse)
        cardRegistry.register(shieldUp)
        cardRegistry.register(boltPulse)

        context("Captain America, Living Legend") {

            test("a creature you control tapped on your turn is untapped again") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Captain America, Living Legend")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Tap Pulse")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("the trigger untapped it") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe false
                }
            }

            test("a creature tapped by regenerating is untapped — regeneration is a real tap") {
                // CR 701.19a: "Regenerate [permanent]" means "…remove all damage marked on it and
                // its controller taps it". That tap is a transition like any other, so it fires this
                // card's trigger. It used to be open-coded and event-free, and the trigger missed it.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Captain America, Living Legend")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Shield Up")
                    .withCardInHand(1, "Bolt Pulse")
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                game.castSpell(1, "Shield Up", targetId = bears).error shouldBe null
                game.resolveStack()
                withClue("banking the shield is not itself a tap") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe false
                }

                // 3 damage to a 2/2 is lethal; regeneration replaces the destruction and taps it.
                game.castSpell(1, "Bolt Pulse", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("regeneration saved it from the destruction") {
                    game.findPermanent("Grizzly Bears").shouldNotBeNull()
                }
                withClue("regeneration's tap fired the trigger, which untapped it again") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe false
                }
            }

            test("several creatures tapped in one turn each get untapped") {
                // The per-permanent point of the card: a `oncePerTurn` cap on the ability would
                // answer only the first of these.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Captain America, Living Legend")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardsInHand(1, "Tap Pulse", 2)
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()

                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()
                game.castSpell(1, "Tap Pulse", targetId = wurm).error shouldBe null
                game.resolveStack()

                withClue("both creatures took their own first tap, so both untapped") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe false
                    game.state.getEntity(wurm)?.has<TappedComponent>() shouldBe false
                }
            }

            test("the same creature tapped a second time this turn stays tapped") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Captain America, Living Legend")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardsInHand(1, "Tap Pulse", 2)
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()
                withClue("first tap: the trigger untapped it, so no Untap Pulse is needed") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe false
                }

                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()
                withClue("the second tap this turn is not the first, so it stays tapped") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                }
            }

            test("untapping and re-tapping in response fizzles the trigger (CR 603.4's second check)") {
                // The printed "if" is an intervening-`if`, so it is checked when the trigger event
                // occurs *and again as the ability resolves*. This is the only case where the two
                // checks disagree, and therefore the only test that can tell a real second check
                // from a frozen copy of the first: hold the trigger on the stack, untap the creature
                // and tap it again underneath it, and by resolution the creature has become tapped
                // twice this turn — so the ability is removed from the stack and the creature stays
                // tapped. An implementation that re-read the triggering event's flag would untap it.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Captain America, Living Legend")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardsInHand(1, "Tap Pulse", 2)
                    .withCardInHand(1, "Untap Pulse")
                    .withLandsOnBattlefield(1, "Plains", 9)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                // Resolve only the Tap Pulse, so the trigger it spawns is left sitting on the stack.
                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.passPriority()
                game.passPriority()
                withClue("the tap happened and Captain America's trigger is waiting to resolve") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                    game.state.stack.isNotEmpty() shouldBe true
                }

                // Respond underneath it: untap, then tap a second time. The second tap is not a
                // first tap, so the event rider stops it spawning a trigger of its own — the stack
                // still holds exactly the one trigger from the first tap.
                game.castSpell(1, "Untap Pulse", targetId = bears).error shouldBe null
                game.passPriority()
                game.passPriority()
                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.passPriority()
                game.passPriority()

                game.resolveStack()
                withClue("the intervening-'if' is false on resolution, so nothing untaps") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                }
            }

            test("a creature you control tapped on an opponent's turn stays tapped") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Captain America, Living Legend")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(2, "Tap Pulse")
                    .withLandsOnBattlefield(2, "Mountain", 4)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                game.castSpell(2, "Tap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("'during your turn' — it is the opponent's turn, so nothing untaps") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                }
            }

            test("a creature an opponent controls is not untapped on your turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Captain America, Living Legend")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(1, "Tap Pulse")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("'a creature you control' — the opponent's Bears stays tapped") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                }
            }

            test("an attacker is untapped but stays in combat and connects") {
                // CR 506.4b: "Tapping or untapping a creature that's already been declared as an
                // attacker or blocker doesn't remove it from combat and doesn't prevent its combat
                // damage." So the attack resolves normally.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Captain America, Living Legend")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.resolveStack()

                withClue("the attack tap was its first this turn, so the trigger untapped it") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe false
                }

                // passUntilPhase auto-submits an empty blocker declaration on the way through.
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                withClue("it was still an attacking creature, so 2 damage got through") {
                    game.getLifeTotal(2) shouldBe 18
                }
            }

            test("Captain America has vigilance, so attacking does not tap him at all") {
                // Asserted on the *event*, not on the end state: without vigilance he would tap,
                // his own trigger would untap him again, and "he is untapped afterwards" would
                // still hold. Only "no TappedEvent was ever emitted for him" tells the keyword
                // apart from the ability sitting next to it.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Captain America, Living Legend")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cap = game.findPermanent("Captain America, Living Legend").shouldNotBeNull()
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                val result = game.declareAttackers(mapOf("Captain America, Living Legend" to 2))
                result.error shouldBe null

                withClue("declaring him as an attacker never tapped him") {
                    result.events.filterIsInstance<TappedEvent>()
                        .none { it.entityId == cap } shouldBe true
                }
                game.resolveStack()
                game.state.getEntity(cap)?.has<TappedComponent>() shouldBe false
            }

            test("Captain America untaps himself when something else taps him on your turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Captain America, Living Legend")
                    .withCardInHand(1, "Tap Pulse")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cap = game.findPermanent("Captain America, Living Legend").shouldNotBeNull()
                game.castSpell(1, "Tap Pulse", targetId = cap).error shouldBe null
                game.resolveStack()

                withClue("the ANY binding covers 'a creature you control' including himself") {
                    game.state.getEntity(cap)?.has<TappedComponent>() shouldBe false
                }
            }
        }
    }
}
