package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.UnstableGlyphbridge
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Unstable Glyphbridge // Sandswirl Wanderglyph (LCI #41).
 *
 * Front face — "When this artifact enters, if you cast it, for each player, choose a creature
 * with power 2 or less that player controls. Then destroy all creatures except creatures chosen
 * this way." + Craft with artifact {3}{W}{W} (exactly one material).
 *
 * Back face — Sandswirl Wanderglyph, 5/3 Artifact Creature — Golem with Flying,
 * "Whenever an opponent casts a spell during their turn, they can't attack you or planeswalkers
 * you control this turn." and "Each opponent who attacked you or a planeswalker you control this
 * turn can't cast spells."
 *
 * Covers:
 *  1. Cast front face: the ability's controller chooses one power-≤2 creature per player
 *     (active player first); everything else is destroyed, chosen creatures and the artifact
 *     itself survive.
 *  2. Intervening "if you cast it": returned to the battlefield from the graveyard (not cast),
 *     the ETB wipe does not fire at all.
 *  3. Craft end-to-end: exactly-one-artifact material cost (two materials rejected), source
 *     returns transformed as a 5/3 flying Golem, material in exile.
 *  4. Back trigger: an opponent casting a spell during their turn locks their creatures
 *     (including ones cast later that turn) out of attacking; your creatures are unaffected.
 *  5. Back static: an opponent who attacked you this turn can't cast spells; one who didn't
 *     attack still can.
 */
class UnstableGlyphbridgeScenarioTest : FunSpec({

    // Test creatures with distinct printed powers: two eligible (power <= 2), one not.
    val runt = CardDefinition.creature(
        name = "Test Runt",
        manaCost = ManaCost.parse("{W}"),
        subtypes = setOf(Subtype("Human")),
        power = 1,
        toughness = 1,
        oracleText = ""
    )
    val squire = CardDefinition.creature(
        name = "Test Squire",
        manaCost = ManaCost.parse("{1}{W}"),
        subtypes = setOf(Subtype("Human")),
        power = 2,
        toughness = 2,
        oracleText = ""
    )
    val colossus = CardDefinition.creature(
        name = "Test Colossus",
        manaCost = ManaCost.parse("{4}{G}"),
        subtypes = setOf(Subtype("Giant")),
        power = 5,
        toughness = 5,
        oracleText = ""
    )

    // Puts a card from a graveyard onto the battlefield WITHOUT casting it — a genuine
    // "entered, but wasn't cast" event for the intervening-if test (the driver's
    // putPermanentOnBattlefield bypasses events entirely, which would make the negative
    // case vacuous).
    val reanimateArtifact = card("Test Artifact Reanimation") {
        manaCost = "{B}"
        colorIdentity = "B"
        typeLine = "Sorcery"
        oracleText = "Return target card from a graveyard to the battlefield."
        spell {
            val t = target("target card in a graveyard", Targets.CardInGraveyard)
            effect = Effects.PutOntoBattlefield(t)
        }
    }

    fun setup(startingPlayer: Int = 0): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(runt, squire, colossus, reanimateArtifact))
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 40),
            skipMulligans = true,
            startingPlayer = startingPlayer
        )
        return driver
    }

    // The front face's only activated ability is the Craft.
    fun craftAbilityId() = UnstableGlyphbridge.activatedAbilities.single().id

    fun awaitSelection(driver: GameTestDriver): SelectCardsDecision {
        var guard = 0
        while (driver.pendingDecision !is SelectCardsDecision && guard++ < 10) {
            driver.bothPass()
        }
        return driver.pendingDecision as? SelectCardsDecision
            ?: error("Expected a SelectCardsDecision; got ${driver.pendingDecision}")
    }

    fun battlefield(driver: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId) =
        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD))

    test("cast from hand: controller picks one power-2-or-less creature per player, all other creatures are destroyed") {
        val driver = setup()
        val p1 = driver.player1
        val p2 = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val runt1 = driver.putCreatureOnBattlefield(p1, "Test Runt")
        val squire1 = driver.putCreatureOnBattlefield(p1, "Test Squire")
        val colossus1 = driver.putCreatureOnBattlefield(p1, "Test Colossus")
        val runt2 = driver.putCreatureOnBattlefield(p2, "Test Runt")
        val squire2 = driver.putCreatureOnBattlefield(p2, "Test Squire")
        val colossus2 = driver.putCreatureOnBattlefield(p2, "Test Colossus")

        val glyph = driver.putCardInHand(p1, "Unstable Glyphbridge")
        driver.giveMana(p1, Color.WHITE, 5)
        driver.castSpell(p1, glyph).error shouldBe null

        // Resolve the artifact spell; the ETB trigger then resolves and pauses per player.
        // Active player first: the controller (p1) chooses among p1's eligible creatures.
        val first = awaitSelection(driver)
        withClue("the ability's controller does all the choosing (\"for each player, choose\")") {
            first.playerId shouldBe p1
        }
        withClue("only power-2-or-less creatures p1 controls are selectable") {
            first.options shouldContainExactlyInAnyOrder listOf(runt1, squire1)
        }
        driver.submitCardSelection(p1, listOf(squire1)).error shouldBe null

        // Second iteration: p1 also chooses for p2, among p2's eligible creatures.
        val second = awaitSelection(driver)
        withClue("the controller (not the iterated player) also chooses for the opponent") {
            second.playerId shouldBe p1
        }
        withClue("only power-2-or-less creatures p2 controls are selectable") {
            second.options shouldContainExactlyInAnyOrder listOf(runt2, squire2)
        }
        driver.submitCardSelection(p1, listOf(runt2)).error shouldBe null

        withClue("chosen creatures survive") {
            battlefield(driver, p1) shouldContain squire1
            battlefield(driver, p2) shouldContain runt2
        }
        withClue("every other creature is destroyed") {
            driver.getGraveyard(p1) shouldContain runt1
            driver.getGraveyard(p1) shouldContain colossus1
            driver.getGraveyard(p2) shouldContain squire2
            driver.getGraveyard(p2) shouldContain colossus2
        }
        withClue("the Glyphbridge itself is an artifact, not a creature — it survives") {
            battlefield(driver, p1) shouldContain glyph
        }
    }

    test("intervening if: entering without being cast (returned from graveyard) does not fire the wipe") {
        val driver = setup()
        val p1 = driver.player1
        val p2 = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val runt1 = driver.putCreatureOnBattlefield(p1, "Test Runt")
        val squire2 = driver.putCreatureOnBattlefield(p2, "Test Squire")
        val glyphCard = driver.putCardInGraveyard(p1, "Unstable Glyphbridge")

        val reanimate = driver.putCardInHand(p1, "Test Artifact Reanimation")
        driver.giveMana(p1, Color.BLACK, 1)
        driver.submit(
            CastSpell(
                playerId = p1,
                cardId = reanimate,
                targets = listOf(ChosenTarget.Card(glyphCard, p1, Zone.GRAVEYARD)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).error shouldBe null
        driver.bothPass()

        withClue("the Glyphbridge entered the battlefield (front face) via the reanimation") {
            battlefield(driver, p1) shouldContain glyphCard
            driver.state.getEntity(glyphCard)!!.get<CardComponent>()!!.name shouldBe "Unstable Glyphbridge"
        }
        withClue("no choose-and-destroy happened: no decision was raised, all creatures alive") {
            driver.pendingDecision shouldBe null
            battlefield(driver, p1) shouldContain runt1
            battlefield(driver, p2) shouldContain squire2
            driver.getGraveyard(p1) shouldNotContain runt1
            driver.getGraveyard(p2) shouldNotContain squire2
        }
    }

    test("craft with artifact: exactly one material (two rejected), returns transformed as a 5/3 flying Golem") {
        val driver = setup()
        val p1 = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val glyph = driver.putPermanentOnBattlefield(p1, "Unstable Glyphbridge")
        val frogmite = driver.putPermanentOnBattlefield(p1, "Frogmite")
        val myr = driver.putPermanentOnBattlefield(p1, "Palladium Myr")
        driver.giveMana(p1, Color.WHITE, 5)

        withClue("\"Craft with artifact\" is exactly one material — two are rejected") {
            val tooMany = driver.submit(
                ActivateAbility(
                    playerId = p1,
                    sourceId = glyph,
                    abilityId = craftAbilityId(),
                    costPayment = AdditionalCostPayment(exiledCards = listOf(frogmite, myr))
                )
            )
            tooMany.isSuccess shouldBe false
        }

        driver.submitSuccess(
            ActivateAbility(
                playerId = p1,
                sourceId = glyph,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(frogmite))
            )
        )
        driver.bothPass()

        withClue("the material was exiled") {
            driver.state.getZone(ZoneKey(p1, Zone.EXILE)) shouldContain frogmite
        }

        val container = driver.state.getEntity(glyph)
        container.shouldNotBeNull()
        val cardComponent = container.get<CardComponent>()
        cardComponent.shouldNotBeNull()
        withClue("returned transformed as the back face") {
            cardComponent.name shouldBe "Sandswirl Wanderglyph"
            cardComponent.typeLine.cardTypes shouldBe setOf(CardType.ARTIFACT, CardType.CREATURE)
            cardComponent.typeLine.subtypes shouldContain Subtype.GOLEM
            container.get<DoubleFacedComponent>()!!.currentFace shouldBe DoubleFacedComponent.Face.BACK
        }
        withClue("back face is a 5/3 with flying") {
            driver.state.projectedState.getPower(glyph) shouldBe 5
            driver.state.projectedState.getToughness(glyph) shouldBe 3
            driver.state.projectedState.hasKeyword(glyph, Keyword.FLYING) shouldBe true
        }
    }

    test("back trigger: an opponent who casts a spell during their turn can't attack this turn") {
        // p2 is the active player; p1 controls Sandswirl Wanderglyph.
        val driver = setup(startingPlayer = 1)
        val p1 = driver.player1
        val p2 = driver.player2
        driver.activePlayer shouldBe p2

        driver.putPermanentOnBattlefield(p1, "Sandswirl Wanderglyph")
        val defenderRunt = driver.putCreatureOnBattlefield(p1, "Test Runt")
        val attackerSquire = driver.putCreatureOnBattlefield(p2, "Test Squire")
        driver.removeSummoningSickness(attackerSquire)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // p2 casts a spell during their own turn -> the trigger fires and resolves.
        val spell = driver.putCardInHand(p2, "Test Runt")
        driver.giveMana(p2, Color.WHITE, 1)
        driver.castSpell(p2, spell).error shouldBe null
        driver.bothPass() // resolve the trigger (on top of the creature spell)
        driver.bothPass() // resolve the creature spell

        withClue("the casting opponent's creatures can't attack this turn") {
            driver.state.projectedState.cantAttack(attackerSquire) shouldBe true
        }
        withClue("the creature they cast after the trigger resolved is restricted too (Rule 611.2c)") {
            val castRunt = driver.findPermanent(p2, "Test Runt")!!
            driver.state.projectedState.cantAttack(castRunt) shouldBe true
        }
        withClue("your own creatures are unaffected") {
            driver.state.projectedState.cantAttack(defenderRunt) shouldBe false
        }

        // Force the declare-attackers step (the engine would otherwise skip it with no legal
        // attacker) and verify the declaration is rejected.
        driver.replaceState(driver.state.copy(phase = Phase.COMBAT, step = Step.DECLARE_ATTACKERS))
        val attack = driver.declareAttackers(p2, mapOf(attackerSquire to p1))
        withClue("declaring the restricted creature as an attacker is rejected") {
            attack.error shouldNotBe null
        }
    }

    test("back static: an opponent who attacked you this turn can't cast spells; one who didn't still can") {
        // Part 1 — the opponent attacks, then tries to cast: rejected.
        run {
            val driver = setup(startingPlayer = 1)
            val p1 = driver.player1
            val p2 = driver.player2

            driver.putPermanentOnBattlefield(p1, "Sandswirl Wanderglyph")
            val attackerSquire = driver.putCreatureOnBattlefield(p2, "Test Squire")
            driver.removeSummoningSickness(attackerSquire)
            val spell = driver.putCardInHand(p2, "Test Runt")

            driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
            driver.declareAttackers(p2, mapOf(attackerSquire to p1)).error shouldBe null
            driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

            driver.giveMana(p2, Color.WHITE, 1)
            val cast = driver.castSpell(p2, spell)
            withClue("the attacker is locked out of casting for the rest of the turn") {
                cast.isSuccess shouldBe false
                cast.error shouldNotBe null
            }
        }

        // Part 2 — no attack this turn: the opponent can cast normally.
        run {
            val driver = setup(startingPlayer = 1)
            val p1 = driver.player1
            val p2 = driver.player2

            driver.putPermanentOnBattlefield(p1, "Sandswirl Wanderglyph")
            val spell = driver.putCardInHand(p2, "Test Runt")

            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
            driver.giveMana(p2, Color.WHITE, 1)
            withClue("an opponent who hasn't attacked is not restricted") {
                driver.castSpell(p2, spell).error shouldBe null
            }
        }
    }
})
