import { test, expect } from '../../fixtures/scenarioFixture'

test('Chandra shows minus X, chooses loyalty to spend, and damages a creature', async ({ createGame }) => {
  const { player1, player2 } = await createGame({
    player1: {
      battlefield: [{ name: 'Chandra Nalaar', counters: { LOYALTY: 6 } }],
      library: ['Mountain', 'Mountain', 'Mountain'],
    },
    player2: {
      battlefield: [{ name: 'Grizzly Bears' }],
      library: ['Island', 'Island', 'Island'],
    },
    phase: 'PRECOMBAT_MAIN',
    activePlayer: 1,
  })
  const p1 = player1.gamePage
  await p1.clickCard('Chandra Nalaar')
  const ability = player1.page.getByRole('button').filter({ hasText: 'deals X damage to target creature' })
  await expect(ability.locator('.ms-loyalty-down.ms-loyalty-x')).toBeVisible()
  await p1.screenshot('Chandra loyalty menu with minus X')
  await ability.click()
  await expect(player1.page.getByText('Maximum X: 6')).toBeVisible()
  await p1.screenshot('Chandra X picker capped at six loyalty')
  await p1.selectXValue(2)
  await p1.selectTarget('Grizzly Bears')
  await p1.confirmTargets()
  await player2.gamePage.resolveStack('Chandra Nalaar ability')
  await p1.expectNotOnBattlefield('Grizzly Bears')
  await player2.gamePage.expectNotOnBattlefield('Grizzly Bears')
})
