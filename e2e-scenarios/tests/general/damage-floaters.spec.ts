import { test, expect } from '../../fixtures/scenarioFixture'

/**
 * Damage dealt to a permanent floats a number over it, the way a life change already does over a
 * player's life display. Two rules are worth guarding beyond "a number appeared":
 *
 *  - only survivors get one. A creature that died to the damage already announced itself by
 *    leaving the board, and a number left behind at its old position would end up labelling
 *    whichever neighbour slid into the vacated slot;
 *  - the number tracks its card. The board reflows the instant anything leaves it, so a floater
 *    anchored to a remembered point drifts onto a different card mid-flight.
 *
 * The floaters live ~650ms, so the test records them with a MutationObserver installed before
 * combat rather than racing a locator against them.
 */
test.describe('Damage floaters', () => {
  test('a surviving permanent floats its own number and carries it through the reflow', async ({
    createGame,
  }) => {
    const { player1, player2 } = await createGame({
      player1Name: 'Attacker',
      player2Name: 'Defender',
      player1: {
        // 3/3 attacks into a 2/2: the Giant survives at -2, the Bears dies and gets no number.
        // The Bears leaving is what makes the row reflow under the Giant's floater.
        battlefield: [{ name: 'Hill Giant', tapped: false, summoningSickness: false }],
      },
      player2: {
        battlefield: [
          { name: 'Grizzly Bears' },
          // A second blocker-sized body so the opponent's row visibly repacks when the Bears dies.
          { name: 'Glory Seeker' },
        ],
        lifeTotal: 20,
      },
      phase: 'PRECOMBAT_MAIN',
      activePlayer: 1,
    })

    const p1 = player1.gamePage
    const p2 = player2.gamePage

    // Record every floating number that appears, and re-measure each one on every frame it lives
    // for. Measuring only the insertion point would prove nothing: the first render happens while
    // the pre-damage DOM is still up, so even a stale anchor lands correctly on that one frame.
    await player1.page.evaluate(() => {
      const w = window as unknown as {
        __floaters: { text: string; x: number; y: number; drift: number }[]
      }
      w.__floaters = []
      new MutationObserver((records) => {
        for (const record of records) {
          for (const node of record.addedNodes) {
            if (!(node instanceof HTMLElement)) continue
            const text = (node.textContent ?? '').trim()
            if (!/^[-+]\d+$/.test(text)) continue
            const rect = node.getBoundingClientRect()
            const entry = { text, x: rect.left + rect.width / 2, y: rect.top + rect.height / 2, drift: 0 }
            w.__floaters.push(entry)
            const track = () => {
              if (!node.isConnected) return
              const now = node.getBoundingClientRect()
              entry.drift = Math.max(
                entry.drift,
                Math.abs(now.left + now.width / 2 - entry.x),
              )
              requestAnimationFrame(track)
            }
            requestAnimationFrame(track)
          }
        }
      }).observe(document.body, { childList: true, subtree: true })
    })

    await p1.pass()
    await p1.attackAll()
    await p2.declareBlocker('Grizzly Bears', 'Hill Giant')
    await p2.confirmBlockers()

    await p1.expectNotOnBattlefield('Grizzly Bears')
    await p1.expectLifeTotal(player2.playerId, 20)

    // Let the floaters live out their ~650ms so the drift tracking has something to see.
    await player1.page.waitForTimeout(900)
    const floaters = await player1.page.evaluate(
      () => (window as unknown as {
        __floaters: { text: string; x: number; y: number; drift: number }[]
      }).__floaters,
    )

    // Exactly one number: the survivor's. Nothing for the dead Bears, nothing for the untouched
    // life total.
    expect(floaters.map((f) => f.text)).toEqual(['-2'])

    // It is over the Giant, and stays over it. The Giant's own card doesn't move here, but the
    // assertion is against its live position after the board settled, not the pre-combat one.
    const giantBox = await player1.page.locator('img[alt="Hill Giant"]').first().boundingBox()
    expect(giantBox).not.toBeNull()
    const giantX = giantBox!.x + giantBox!.width / 2
    expect(Math.abs(floaters[0]!.x - giantX)).toBeLessThan(60)
    // Only the deliberate vertical float is allowed; sideways travel means it lost its card.
    expect(floaters[0]!.drift).toBeLessThan(20)

    await p1.screenshot('After combat')
  })

  /**
   * The animation layers sit above every modal so they can clear one — which means they also sit
   * above the Victory/Defeat overlay. The killing blow's own life floater is queued by the state
   * update that immediately precedes the GameOver message, so without clearing it, the game ends
   * with "-3" hanging over the result card.
   */
  test('no floating numbers survive onto the game-over overlay', async ({ createGame }) => {
    const { player1, player2 } = await createGame({
      player1Name: 'Attacker',
      player2Name: 'Defender',
      player1: {
        battlefield: [{ name: 'Hill Giant', tapped: false, summoningSickness: false }],
      },
      // Exactly lethal: the damage, the life floater, and the end of the game all land together.
      player2: { lifeTotal: 3 },
      phase: 'PRECOMBAT_MAIN',
      activePlayer: 1,
    })

    const p1 = player1.gamePage

    await p1.pass()
    await p1.attackAll()

    await expect(player1.page.getByText('Victory!')).toBeVisible({ timeout: 15_000 })

    // Well inside a floater's ~800ms life, so an uncleared one would still be on screen.
    await player1.page.waitForTimeout(300)
    const stillFloating = await player1.page.evaluate(() =>
      [...document.querySelectorAll('body *')]
        .filter((el) => el.children.length === 0 && /^[-+]\d+$/.test((el.textContent ?? '').trim()))
        .filter((el) => getComputedStyle(el).position === 'fixed')
        .map((el) => el.textContent?.trim() ?? ''),
    )
    expect(stillFloating).toEqual([])

    await p1.screenshot('Game over, no floaters')
  })
})
