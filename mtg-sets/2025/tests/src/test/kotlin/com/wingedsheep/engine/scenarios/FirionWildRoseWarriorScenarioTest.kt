package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.FirionWildRoseWarrior
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldHaveSize
import com.wingedsheep.sdk.model.EntityId

/**
 * Firion, Wild Rose Warrior (FIN).
 *
 * - "Equipped creatures you control have haste." (a projected [Keyword.HASTE] grant over the
 *   equipped creatures you control).
 * - "Whenever a nontoken Equipment you control enters, create a token that's a copy of it, except
 *   it has 'This Equipment's equip abilities cost {2} less to activate.' Sacrifice that token at
 *   the beginning of the next upkeep." — the new
 *   [com.wingedsheep.sdk.scripting.effects.CreateTokenCopyOfTargetEffect.addedStaticAbilities] +
 *   [com.wingedsheep.sdk.scripting.ReduceEquipCost] `onlyOwnEquip` path.
 *
 * An inline Equip {3} test equipment makes the token's {2} discount observable (equips paying {1}),
 * and lets us prove the discount is scoped to the token itself (the printed blade still needs {3}).
 */
class FirionWildRoseWarriorScenarioTest : FunSpec({

    // Inline Equip {3} equipment so the token's {2} reduction shows as a {1} payment.
    val testBlade = card("Firion Test Blade") {
        manaCost = "{1}"
        typeLine = "Artifact — Equipment"
        oracleText = "Equipped creature gets +1/+0.\nEquip {3}"
        equipAbility("{3}")
    }
    val equipId = testBlade.activatedAbilities.first().id

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(FirionWildRoseWarrior, testBlade))
        return driver
    }

    // Player 1 may not be active at game start (random turn order) — advance until it is.
    fun GameTestDriver.advanceToPlayer1(targetStep: Step) {
        passPriorityUntil(targetStep)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(targetStep)
            safety++
        }
    }

    // Cast resolves the spell, then Firion's triggered ability lands back on the stack — drain both.
    fun GameTestDriver.resolveStack() {
        var safety = 0
        while (stackSize > 0 && pendingDecision == null && safety < 20) {
            bothPass()
            safety++
        }
    }

    fun GameTestDriver.testBladesOnBattlefield(playerId: EntityId): List<EntityId> =
        state.getBattlefield(playerId).filter {
            state.getEntity(it)?.get<CardComponent>()?.name == "Firion Test Blade"
        }

    fun GameTestDriver.tokenBlade(playerId: EntityId): EntityId =
        testBladesOnBattlefield(playerId).first { state.getEntity(it)?.get<TokenComponent>() != null }

    fun GameTestDriver.printedBlade(playerId: EntityId): EntityId =
        testBladesOnBattlefield(playerId).first { state.getEntity(it)?.get<TokenComponent>() == null }

    test("equipped creatures you control have haste; unequipped ones do not") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        driver.putCreatureOnBattlefield(driver.player1, "Firion, Wild Rose Warrior")
        val equippedBear = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val plainBear = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val blade = driver.putPermanentOnBattlefield(driver.player1, "Firion Test Blade")

        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        driver.giveColorlessMana(driver.player1, 3)
        driver.submit(
            ActivateAbility(driver.player1, blade, equipId, targets = listOf(ChosenTarget.Permanent(equippedBear)))
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.state.getEntity(blade)?.get<AttachedToComponent>()?.targetId shouldBe equippedBear

        val projected = projector.project(driver.state)
        projected.hasKeyword(equippedBear, Keyword.HASTE) shouldBe true
        projected.hasKeyword(plainBear, Keyword.HASTE) shouldBe false
    }

    test("a nontoken Equipment entering creates a sacrificing token copy of it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        driver.putCreatureOnBattlefield(driver.player1, "Firion, Wild Rose Warrior")
        val bladeInHand = driver.putCardInHand(driver.player1, "Firion Test Blade")

        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        driver.giveColorlessMana(driver.player1, 1)
        driver.castSpell(driver.player1, bladeInHand).isSuccess shouldBe true
        driver.resolveStack()

        // The printed blade plus its token copy — two Equipment, exactly one of them a token.
        driver.testBladesOnBattlefield(driver.player1) shouldHaveSize 2
        driver.state.getEntity(driver.tokenBlade(driver.player1))?.get<TokenComponent>() shouldBe TokenComponent
    }

    test("the token copy's equip abilities cost {2} less, but other Equipment is unaffected") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        driver.putCreatureOnBattlefield(driver.player1, "Firion, Wild Rose Warrior")
        val bear = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val bladeInHand = driver.putCardInHand(driver.player1, "Firion Test Blade")

        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        driver.giveColorlessMana(driver.player1, 1)
        driver.castSpell(driver.player1, bladeInHand).isSuccess shouldBe true
        driver.resolveStack()

        val token = driver.tokenBlade(driver.player1)
        val printed = driver.printedBlade(driver.player1)

        // The printed blade's own equip still costs the full {3}: only {1} available → fails.
        driver.giveColorlessMana(driver.player1, 1)
        driver.submit(
            ActivateAbility(driver.player1, printed, equipId, targets = listOf(ChosenTarget.Permanent(bear)))
        ).isSuccess shouldBe false

        // The token's equip is reduced by {2}: the same {1} pays for it.
        driver.submit(
            ActivateAbility(driver.player1, token, equipId, targets = listOf(ChosenTarget.Permanent(bear)))
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.state.getEntity(token)?.get<AttachedToComponent>()?.targetId shouldBe bear
    }

    test("the token copy is sacrificed at the beginning of the next upkeep") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        driver.putCreatureOnBattlefield(driver.player1, "Firion, Wild Rose Warrior")
        val bladeInHand = driver.putCardInHand(driver.player1, "Firion Test Blade")

        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        driver.giveColorlessMana(driver.player1, 1)
        driver.castSpell(driver.player1, bladeInHand).isSuccess shouldBe true
        driver.resolveStack()

        val token = driver.tokenBlade(driver.player1)

        // Advance to the next upkeep — the delayed sacrifice fires for the token, not the printed blade.
        driver.passPriorityUntil(Step.UPKEEP)
        driver.bothPass()

        driver.state.getEntity(token)?.get<TokenComponent>() shouldBe null
        driver.testBladesOnBattlefield(driver.player1) shouldHaveSize 1
    }
})
