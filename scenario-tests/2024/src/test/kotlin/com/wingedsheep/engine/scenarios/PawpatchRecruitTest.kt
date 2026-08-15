package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.BrightbladeStoat
import com.wingedsheep.mtg.sets.definitions.blb.cards.PawpatchRecruit
import com.wingedsheep.mtg.sets.definitions.ons.cards.GlorySeeker
import com.wingedsheep.mtg.sets.definitions.ons.cards.KamahlFistOfKrosa
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * "Whenever a creature you control becomes the target of a spell or ability
 * an opponent controls, put a +1/+1 counter on target creature you control
 * other than that creature."
 *
 * "That creature" = the creature that was targeted by the opponent (the triggering
 * entity), NOT the Pawpatch Recruit that owns the ability. If an opponent targets
 * one of your creatures, you must put the counter on a *different* creature you
 * control — including Pawpatch Recruit itself if it wasn't the one that was
 * targeted.
 */
class PawpatchRecruitTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(PawpatchRecruit, BrightbladeStoat, GlorySeeker))
        return driver
    }

    test("counter target excludes the creature that was targeted, not the Pawpatch Recruit source") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Active player controls Pawpatch Recruit plus two other creatures.
        val pawpatch = driver.putCreatureOnBattlefield(activePlayer, "Pawpatch Recruit")
        val stoat = driver.putCreatureOnBattlefield(activePlayer, "Brightblade Stoat")
        val seeker = driver.putCreatureOnBattlefield(activePlayer, "Glory Seeker")

        // Opponent prepares a Lightning Bolt to target the Stoat.
        driver.giveMana(opponent, Color.RED, 1)
        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")

        // Pass priority so the opponent can cast the bolt at instant speed.
        driver.passPriority(activePlayer)
        val cast = driver.castSpellWithTargets(
            opponent, bolt, listOf(ChosenTarget.Permanent(stoat))
        )
        cast.error shouldBe null

        // Pawpatch's trigger fires and pauses for target selection by the active player.
        driver.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()

        val decision = driver.pendingDecision as ChooseTargetsDecision
        decision.playerId shouldBe activePlayer
        val legalTargets = decision.legalTargets[0] ?: emptyList()

        // "Target creature you control other than that creature" — *that creature* is the
        // Stoat (the one that became the target). Pawpatch itself is a legal target; the
        // Stoat is not.
        legalTargets shouldContain pawpatch
        legalTargets shouldContain seeker
        legalTargets shouldNotContain stoat
    }

    test("triggers when an opponent targets an animated land creature you control") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val pawpatch = driver.putCreatureOnBattlefield(activePlayer, "Pawpatch Recruit")
        val kamahl = driver.putCreatureOnBattlefield(activePlayer, "Kamahl, Fist of Krosa")
        val forest = driver.putLandOnBattlefield(activePlayer, "Forest")
        driver.removeSummoningSickness(kamahl)

        // Animate the Forest into a 1/1 creature (it's still a land).
        driver.giveMana(activePlayer, Color.GREEN, 1)
        val animate = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = kamahl,
                abilityId = KamahlFistOfKrosa.activatedAbilities[0].id,
                targets = listOf(ChosenTarget.Permanent(forest))
            )
        )
        animate.isSuccess shouldBe true
        driver.bothPass()

        // Opponent bolts the animated Forest — a creature the active player controls,
        // so Pawpatch Recruit's trigger must fire even though the Forest is only a
        // creature via a continuous effect.
        driver.giveMana(opponent, Color.RED, 1)
        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.passPriority(activePlayer)
        val cast = driver.castSpellWithTargets(
            opponent, bolt, listOf(ChosenTarget.Permanent(forest))
        )
        cast.error shouldBe null

        driver.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
        val decision = driver.pendingDecision as ChooseTargetsDecision
        decision.playerId shouldBe activePlayer
        val legalTargets = decision.legalTargets[0] ?: emptyList()

        // "Other than that creature" excludes the animated Forest; Pawpatch and
        // Kamahl are legal.
        legalTargets shouldContain pawpatch
        legalTargets shouldContain kamahl
        legalTargets shouldNotContain forest
    }
})
