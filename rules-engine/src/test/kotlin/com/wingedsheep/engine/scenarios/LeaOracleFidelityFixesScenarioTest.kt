package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Regression net for the Alpha (LEA) oracle-fidelity bugs found in the set audit: a family of
 * mtgish-generated cards silently dropped a targeting constraint (color / blocking / ownership /
 * mana value X) or a whole modal mode, making each card strictly stronger than printed.
 *
 *  - Blue Elemental Blast: "Choose one — Counter target RED spell / Destroy target RED permanent"
 *    (was: counter ANY spell, no destroy mode at all). Red Elemental Blast is the exact mirror.
 *  - Deathgrip: "{B}{B}: Counter target GREEN spell" (was: any spell).
 *  - Northern Paladin: "{W}{W}, {T}: Destroy target BLACK permanent" (was: any permanent).
 *  - Righteousness: "Target BLOCKING creature gets +7/+7" (was: any creature).
 *  - Spell Blast: "Counter target spell WITH MANA VALUE X" (was: any spell, X ignored).
 *  - Regrowth: "Return target card from YOUR graveyard" (was: any graveyard).
 */
class LeaOracleFidelityFixesScenarioTest : ScenarioTestBase() {

    private val deathgripAbilityId by lazy {
        cardRegistry.getCard("Deathgrip")!!.activatedAbilities[0].id
    }
    private val paladinAbilityId by lazy {
        cardRegistry.getCard("Northern Paladin")!!.activatedAbilities[0].id
    }

    init {
        context("Blue Elemental Blast — modal, red-only") {
            test("counter mode counters a red spell") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Blue Elemental Blast")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withCardInHand(2, "Lightning Bolt")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
                game.passPriority() // P2 passes so P1 can respond

                val boltId = game.state.stack.first()
                val handId = game.state.getHand(game.player1Id).first()
                val result = game.execute(
                    CastSpell(
                        game.player1Id, handId,
                        targets = listOf(ChosenTarget.Spell(boltId)),
                        chosenModes = listOf(0),
                        modeTargetsOrdered = listOf(listOf(ChosenTarget.Spell(boltId)))
                    )
                )
                withClue("Countering a red spell should be legal: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
                withClue("Lightning Bolt should be countered") {
                    game.isInGraveyard(2, "Lightning Bolt") shouldBe true
                }
            }

            test("counter mode cannot target a non-red spell") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Blue Elemental Blast")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withCardInHand(2, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Forest", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.passPriority()

                val bearsId = game.state.stack.first()
                val handId = game.state.getHand(game.player1Id).first()
                val result = game.execute(
                    CastSpell(
                        game.player1Id, handId,
                        targets = listOf(ChosenTarget.Spell(bearsId)),
                        chosenModes = listOf(0),
                        modeTargetsOrdered = listOf(listOf(ChosenTarget.Spell(bearsId)))
                    )
                )
                withClue("A green spell must not be a legal target for the counter mode") {
                    result.error shouldNotBe null
                }
            }

            test("destroy mode destroys a red permanent but cannot target a non-red one") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Blue Elemental Blast")
                    .withCardInHand(1, "Blue Elemental Blast")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardOnBattlefield(2, "Gray Ogre")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("A green permanent must not be a legal target for the destroy mode") {
                    game.castSpellWithMode(1, "Blue Elemental Blast", 1, bears).error shouldNotBe null
                }

                val ogre = game.findPermanent("Gray Ogre")!!
                val result = game.castSpellWithMode(1, "Blue Elemental Blast", 1, ogre)
                withClue("Destroying a red permanent should be legal: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
                withClue("Gray Ogre (red) should be destroyed") {
                    game.isOnBattlefield("Gray Ogre") shouldBe false
                    game.isInGraveyard(2, "Gray Ogre") shouldBe true
                }
            }
        }

        context("Deathgrip — counters only green spells") {
            test("counters a green spell, rejects a red spell") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Deathgrip")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withCardInHand(2, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Forest", 2)
                    .withCardInHand(2, "Lightning Bolt")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val deathgrip = game.findPermanent("Deathgrip")!!

                // Red spell on the stack: not a legal target.
                game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
                game.passPriority()
                val boltId = game.state.stack.first()
                withClue("A red spell must not be targetable by Deathgrip") {
                    game.execute(
                        ActivateAbility(
                            game.player1Id, deathgrip, deathgripAbilityId,
                            targets = listOf(ChosenTarget.Spell(boltId))
                        )
                    ).error shouldNotBe null
                }
                game.resolveStack() // bolt resolves

                // Green spell on the stack: countered.
                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.passPriority()
                val bearsId = game.state.stack.first()
                val result = game.execute(
                    ActivateAbility(
                        game.player1Id, deathgrip, deathgripAbilityId,
                        targets = listOf(ChosenTarget.Spell(bearsId))
                    )
                )
                withClue("Countering a green spell should be legal: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
                withClue("Grizzly Bears should be countered") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
            }
        }

        context("Northern Paladin — destroys only black permanents") {
            test("destroys a black creature, rejects a non-black one") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Northern Paladin", tapped = false, summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardOnBattlefield(2, "Scathe Zombies")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val paladin = game.findPermanent("Northern Paladin")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("A green permanent must not be a legal target") {
                    game.execute(
                        ActivateAbility(
                            game.player1Id, paladin, paladinAbilityId,
                            targets = listOf(ChosenTarget.Permanent(bears))
                        )
                    ).error shouldNotBe null
                }

                val zombies = game.findPermanent("Scathe Zombies")!!
                val result = game.execute(
                    ActivateAbility(
                        game.player1Id, paladin, paladinAbilityId,
                        targets = listOf(ChosenTarget.Permanent(zombies))
                    )
                )
                withClue("Destroying a black permanent should be legal: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
                withClue("Scathe Zombies (black) should be destroyed") {
                    game.isOnBattlefield("Scathe Zombies") shouldBe false
                }
            }
        }

        context("Righteousness — target blocking creature only") {
            test("cannot target a creature that isn't blocking") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Righteousness")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("A non-blocking creature must not be a legal target") {
                    game.castSpell(1, "Righteousness", bears).error shouldNotBe null
                }
            }

            test("gives a blocking creature +7/+7") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Gray Ogre", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(2, "Righteousness")
                    .withLandsOnBattlefield(2, "Plains", 1)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Plains")
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Gray Ogre" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Grizzly Bears" to listOf("Gray Ogre"))).error shouldBe null

                // Block declaration is a turn-based action.  Once all declarations and
                // their trigger boundary finish, priority is with the active player.
                game.passPriority() // Player 1 passes; Player 2 may now cast Righteousness.
                val bears = game.findPermanent("Grizzly Bears")!!
                val result = game.castSpell(2, "Righteousness", bears)
                withClue("A blocking creature should be a legal target: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
                withClue("Blocker should be 9/9 (+7/+7 on a 2/2)") {
                    game.state.projectedState.getPower(bears) shouldBe 9
                }
            }
        }

        context("Spell Blast — counters only a spell with mana value X") {
            test("X must equal the target spell's mana value") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Spell Blast")
                    .withCardInHand(1, "Spell Blast")
                    .withLandsOnBattlefield(1, "Island", 8)
                    .withCardInHand(2, "Grizzly Bears") // mana value 2
                    .withLandsOnBattlefield(2, "Forest", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.passPriority()

                val bearsId = game.state.stack.first()
                fun spellBlastInHand() = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == "Spell Blast"
                }

                withClue("X=1 must not legally counter a mana value 2 spell") {
                    game.execute(
                        CastSpell(
                            game.player1Id, spellBlastInHand(),
                            targets = listOf(ChosenTarget.Spell(bearsId)),
                            xValue = 1
                        )
                    ).error shouldNotBe null
                }

                val result = game.execute(
                    CastSpell(
                        game.player1Id, spellBlastInHand(),
                        targets = listOf(ChosenTarget.Spell(bearsId)),
                        xValue = 2
                    )
                )
                withClue("X=2 should legally counter a mana value 2 spell: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
                withClue("Grizzly Bears should be countered") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
            }
        }

        context("Regrowth — your graveyard only") {
            test("cannot target a card in an opponent's graveyard; returns your own") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Regrowth")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardInGraveyard(1, "Giant Growth")
                    .withCardInGraveyard(2, "Lightning Bolt")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("An opponent's graveyard card must not be a legal target") {
                    game.castSpellTargetingGraveyardCard(1, "Regrowth", 2, "Lightning Bolt")
                        .error shouldNotBe null
                }

                val handBefore = game.handSize(1)
                val result = game.castSpellTargetingGraveyardCard(1, "Regrowth", 1, "Giant Growth")
                withClue("Your own graveyard card should be a legal target: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
                withClue("Giant Growth returns to hand (Regrowth cast -1, Giant Growth +1)") {
                    game.handSize(1) shouldBe handBefore
                    game.isInGraveyard(1, "Giant Growth") shouldBe false
                }
            }
        }
    }
}
