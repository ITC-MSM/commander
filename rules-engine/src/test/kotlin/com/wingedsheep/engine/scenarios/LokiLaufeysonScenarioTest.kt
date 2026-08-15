package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.msh.cards.LokiLaufeyson
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.effects.MoveToZoneEffect
import com.wingedsheep.sdk.scripting.effects.StormCopyEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Loki Laufeyson (MSH #143) — {1}{R} Legendary Creature — God Sorcerer Villain, 2/1.
 *
 *   {1}, {T}: When you next cast an instant or sorcery spell with mana value less than or equal to
 *   Loki's power this turn, copy that spell. You may choose new targets for the copy.
 *   Power-up — {4}{R}: Put two +1/+1 counters on Loki.
 *
 * The claim under test is the *dynamic* mana-value cap on the pending copy rider. Every other
 * "copy your next spell" card in the engine caps at a constant (Gadwick's First Duel's "3 or less")
 * or not at all (Howl of the Horde), so the cap here has to be resolved against Loki himself at the
 * moment the spell is cast — which is when the delayed trigger's condition is checked. Two things
 * would break silently without it: a cap that resolves to 0 (no source in the predicate context)
 * would copy nothing, and a cap snapshotted when the rider was created would ignore Loki growing
 * afterwards.
 *
 * Each rider case resolves the stack and asserts the *life totals*, so a scheduled-but-inert copy
 * fails the test; a cheap creature spell pins the `InstantOrSorcery` half of the filter, which the
 * mana-value cases alone would not catch.
 *
 * The last three cases cover the cap once Loki has *left* the battlefield. CR 608.2h makes that a
 * last-known-information read — the rider survives its source (CR 113.7a), so the cap is the power
 * Loki last had in play, which is why the snapshot is stamped at departure and not when the rider
 * was armed. Both directions are pinned: a pumped Loki widens the cap from beyond the grave, and an
 * unpumped one still refuses the same spell, so a stamp that silently captured the wrong moment
 * fails one of the two.
 */
class LokiLaufeysonScenarioTest : FunSpec({

    val riderAbilityId = LokiLaufeyson.activatedAbilities[0].id
    val powerUpAbilityId = LokiLaufeyson.activatedAbilities[1].id

    /** {1}{R} — mana value 2, no targets, so casting it needs no target plumbing. */
    val twoManaInstant = CardDefinition.instant(
        name = "Test Two-Mana Salve",
        manaCost = ManaCost.parse("{1}{R}"),
        oracleText = "You gain 1 life.",
        script = CardScript.spell(effect = GainLifeEffect(1, EffectTarget.Controller))
    )

    /** {2}{R} — mana value 3, one over a base 2/1 Loki and one under a powered-up 4/3 one. */
    val threeManaInstant = CardDefinition.instant(
        name = "Test Three-Mana Salve",
        manaCost = ManaCost.parse("{2}{R}"),
        oracleText = "You gain 1 life.",
        script = CardScript.spell(effect = GainLifeEffect(1, EffectTarget.Controller))
    )

    /**
     * {1}{R} — mana value 2, so it passes the cap but fails the *type* half of the filter. Guards
     * against a regression that kept only `manaValueAtMostDynamic` and dropped `InstantOrSorcery`.
     */
    val twoManaCreature = CardDefinition.creature(
        name = "Test Two-Mana Trickster",
        manaCost = ManaCost.parse("{1}{R}"),
        subtypes = setOf(Subtype("Shapeshifter")),
        power = 1,
        toughness = 1
    )

    /**
     * {4}{B} instant — destroy target creature, used to kill Loki himself. Two constraints shape it:
     *
     * - It must route the kill through the real `ZoneTransitionService` path, since that is what
     *   stamps the rider's last-known-information snapshot. The driver's `moveToGraveyard` is a
     *   blunt zone move that skips it entirely, so it would never exercise the stamp.
     * - Its controller must be the rider's controller, because only the active player holds
     *   priority in their own main phase. So it is deliberately priced at **mana value 5** —
     *   above the cap in every case here (4 for a pumped Loki, 2 for an unpumped one) — and a
     *   non-matching cast leaves the rider armed, exactly as the "cheap creature spell" case above
     *   already pins.
     */
    val selfRemoval = CardDefinition.instant(
        name = "Test Costly Assassination",
        manaCost = ManaCost.parse("{4}{B}"),
        oracleText = "Destroy target creature.",
        script = CardScript.spell(
            effect = MoveToZoneEffect(
                EffectTarget.BoundVariable("target"),
                Zone.GRAVEYARD,
                byDestruction = true
            ),
            TargetCreature(filter = TargetFilter.Creature, id = "target")
        )
    )

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + LokiLaufeyson + twoManaInstant + threeManaInstant + twoManaCreature +
                selfRemoval
        )
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Loki on the battlefield, able to pay the {T} in the rider's cost. */
    fun readyLoki(driver: GameTestDriver, player: EntityId): EntityId {
        val loki = driver.putCreatureOnBattlefield(player, "Loki Laufeyson")
        driver.removeSummoningSickness(loki)
        return loki
    }

    /** Pass until the stack is empty (the spell, then the copy trigger, then the copy itself). */
    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 20) driver.bothPass()
    }

    /** Arm the rider: {1}, {T}, then let the ability resolve. */
    fun armRider(driver: GameTestDriver, player: EntityId, loki: EntityId) {
        driver.giveMana(player, Color.RED, 1)
        driver.submitSuccess(
            ActivateAbility(playerId = player, sourceId = loki, abilityId = riderAbilityId)
        )
        resolveStack(driver)
    }

    fun stormCopies(driver: GameTestDriver): Int =
        driver.state.stack.mapNotNull {
            driver.state.getEntity(it)?.get<TriggeredAbilityOnStackComponent>()
        }.count { it.effect is StormCopyEffect }

    test("the rider copies a spell whose mana value equals Loki's power") {
        val driver = newDriver()
        val player = driver.player1

        val loki = readyLoki(driver, player)
        armRider(driver, player, loki)
        driver.state.pendingSpellCopies.size shouldBe 1

        // Mana value 2 vs. a base 2/1 Loki — "less than or equal to" includes equal.
        val salve = driver.putCardInHand(player, "Test Two-Mana Salve")
        driver.giveMana(player, Color.RED, 2)
        driver.castSpell(player, salve).isSuccess shouldBe true

        withClue("MV 2 <= power 2, so the rider fires") { stormCopies(driver) shouldBe 1 }
        withClue("a one-shot rider is consumed by the spell it copies") {
            driver.state.pendingSpellCopies.size shouldBe 0
        }

        // Behavioral half: the copy has to actually resolve, not merely be scheduled.
        resolveStack(driver)
        withClue("the salve resolves twice — original + copy — so 20 + 1 + 1") {
            driver.getLifeTotal(player) shouldBe 22
        }
    }

    test("the rider ignores a spell whose mana value exceeds Loki's power and stays armed") {
        val driver = newDriver()
        val player = driver.player1

        val loki = readyLoki(driver, player)
        armRider(driver, player, loki)

        val salve = driver.putCardInHand(player, "Test Three-Mana Salve")
        driver.giveMana(player, Color.RED, 3)
        driver.castSpell(player, salve).isSuccess shouldBe true

        withClue("MV 3 > power 2, so nothing is copied") { stormCopies(driver) shouldBe 0 }
        withClue("a non-matching cast must not consume the rider") {
            driver.state.pendingSpellCopies.size shouldBe 1
        }

        resolveStack(driver)
        withClue("only the original salve resolves, so 20 + 1") {
            driver.getLifeTotal(player) shouldBe 21
        }
    }

    test("the rider ignores a cheap creature spell — the filter's type half still applies") {
        val driver = newDriver()
        val player = driver.player1

        val loki = readyLoki(driver, player)
        armRider(driver, player, loki)

        // Mana value 2, so it clears the dynamic cap — but it is not an instant or sorcery.
        val trickster = driver.putCardInHand(player, "Test Two-Mana Trickster")
        driver.giveMana(player, Color.RED, 2)
        driver.castSpell(player, trickster).isSuccess shouldBe true

        withClue("InstantOrSorcery rejects a creature spell even under the cap") {
            stormCopies(driver) shouldBe 0
        }
        withClue("a non-matching cast must not consume the rider") {
            driver.state.pendingSpellCopies.size shouldBe 1
        }
    }

    test("growing Loki after the rider is armed widens what the rider copies") {
        val driver = newDriver()
        val player = driver.player1

        val loki = readyLoki(driver, player)
        armRider(driver, player, loki)

        // Two +1/+1 counters — what the power-up ability puts on him — make Loki a 4/3.
        driver.addComponent(loki, CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 2)))
        driver.state.projectedState.getPower(loki) shouldBe 4

        val salve = driver.putCardInHand(player, "Test Three-Mana Salve")
        driver.giveMana(player, Color.RED, 3)
        driver.castSpell(player, salve).isSuccess shouldBe true

        withClue("the cap is read at cast time, not when the rider was created") {
            stormCopies(driver) shouldBe 1
        }
        driver.state.pendingSpellCopies.size shouldBe 0

        resolveStack(driver)
        withClue("the widened rider actually produces a resolving copy, so 20 + 1 + 1") {
            driver.getLifeTotal(player) shouldBe 22
        }
    }

    /**
     * Kill [loki] with [selfRemoval] so the removal runs through the real zone-transition path,
     * then resolve the stack. Asserts both that he actually left — a test that silently failed to
     * kill him would otherwise masquerade as a passing last-known-information case — and that the
     * rider survived the removal cast unconsumed.
     */
    fun killLoki(driver: GameTestDriver, player: EntityId, loki: EntityId) {
        val removal = driver.putCardInHand(player, "Test Costly Assassination")
        driver.giveMana(player, Color.BLACK, 5)
        driver.castSpell(player, removal, listOf(loki)).isSuccess shouldBe true
        resolveStack(driver)
        withClue("the removal has to have actually resolved for this to be an LKI test") {
            (loki in driver.state.getBattlefield()) shouldBe false
        }
        withClue("the removal is priced above the cap, so it must not have eaten the rider") {
            driver.state.pendingSpellCopies.size shouldBe 1
        }
    }

    test("a dead Loki's rider caps on his last-known power, not his printed one") {
        val driver = newDriver()
        val player = driver.player1

        val loki = readyLoki(driver, player)
        armRider(driver, player, loki)

        // Arm at 2/1, grow to 4/3, then die — the cap must be the 4 he last had on the battlefield.
        driver.addComponent(loki, CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 2)))
        driver.state.projectedState.getPower(loki) shouldBe 4
        killLoki(driver, player, loki)

        withClue("the rider outlives its source (CR 113.7a) — killing Loki must not remove it") {
            driver.state.pendingSpellCopies.size shouldBe 1
        }
        withClue("departure stamps the snapshot with the power he last had, not his printed 2") {
            driver.state.pendingSpellCopies[0].lastKnownSourceSnapshot?.power shouldBe 4
        }

        val salve = driver.putCardInHand(player, "Test Three-Mana Salve")
        driver.giveMana(player, Color.RED, 3)
        driver.castSpell(player, salve).isSuccess shouldBe true

        withClue("MV 3 <= last-known power 4, so the rider still fires (CR 608.2h)") {
            stormCopies(driver) shouldBe 1
        }

        resolveStack(driver)
        withClue("the copy resolves, so 20 + 1 + 1") { driver.getLifeTotal(player) shouldBe 22 }
    }

    test("a dead unpumped Loki's rider still refuses a spell above his last-known power") {
        val driver = newDriver()
        val player = driver.player1

        val loki = readyLoki(driver, player)
        armRider(driver, player, loki)

        // Same shape as the case above, minus the counters: last-known power is 2, so MV 3 misses.
        // Without this, a stamp that defaulted to some permissive value would pass the widening
        // test and go unnoticed.
        killLoki(driver, player, loki)
        driver.state.pendingSpellCopies[0].lastKnownSourceSnapshot?.power shouldBe 2

        val salve = driver.putCardInHand(player, "Test Three-Mana Salve")
        driver.giveMana(player, Color.RED, 3)
        driver.castSpell(player, salve).isSuccess shouldBe true

        withClue("MV 3 > last-known power 2, so nothing is copied") { stormCopies(driver) shouldBe 0 }
        withClue("a non-matching cast must not consume the rider") {
            driver.state.pendingSpellCopies.size shouldBe 1
        }

        resolveStack(driver)
        withClue("only the original salve resolves, so 20 + 1") {
            driver.getLifeTotal(player) shouldBe 21
        }
    }

    test("a dead Loki's rider still copies a spell at exactly his last-known power") {
        val driver = newDriver()
        val player = driver.player1

        val loki = readyLoki(driver, player)
        armRider(driver, player, loki)
        killLoki(driver, player, loki)

        // MV 2 vs. last-known power 2 — "less than or equal to" has to keep including equal once
        // the read is coming from a snapshot rather than from projected state.
        val salve = driver.putCardInHand(player, "Test Two-Mana Salve")
        driver.giveMana(player, Color.RED, 2)
        driver.castSpell(player, salve).isSuccess shouldBe true

        withClue("MV 2 <= last-known power 2") { stormCopies(driver) shouldBe 1 }

        resolveStack(driver)
        withClue("the copy resolves, so 20 + 1 + 1") { driver.getLifeTotal(player) shouldBe 22 }
    }

    test("the power-up ability puts two +1/+1 counters on Loki") {
        val driver = newDriver()
        val player = driver.player1

        val loki = readyLoki(driver, player)
        driver.giveMana(player, Color.RED, 6)
        driver.submitSuccess(
            ActivateAbility(playerId = player, sourceId = loki, abilityId = powerUpAbilityId)
        )
        resolveStack(driver)

        driver.state.getEntity(loki)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
        driver.state.projectedState.getPower(loki) shouldBe 4
        driver.state.projectedState.getToughness(loki) shouldBe 3
    }
})
