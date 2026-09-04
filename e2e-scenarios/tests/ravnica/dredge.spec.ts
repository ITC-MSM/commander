import { test, expect } from '../../fixtures/scenarioFixture'

test.use({ channel: 'chrome' })

for (const acceptScarab of [true, false]) {
  test(`dredge offers each source once, then ${acceptScarab ? 'returns the chosen Scarab' : 'draws normally'}`, async ({ createGame }, testInfo) => {
    const { player1, player2 } = await createGame({
      player1Name: 'Dredge',
      player2Name: 'Opponent',
      player1: {
        battlefield: [{ name: 'Grave-Shell Scarab' }, { name: 'Forest' }],
        graveyard: ['Greater Mossdog', 'Darkblast', 'Moldervine Cloak'],
        library: ['Forest', 'Island', 'Swamp', 'Mountain'],
      },
      player2: { library: ['Forest', 'Island'] },
      phase: 'PRECOMBAT_MAIN',
      activePlayer: 1,
    })
    const p1 = player1.gamePage
    await p1.clickCard('Grave-Shell Scarab')
    await p1.selectAction('Sacrifice this permanent: Draw a card')
    await player2.gamePage.pass()

    for (const [name, amount] of [['Greater Mossdog', 3], ['Darkblast', 3], ['Moldervine Cloak', 2]] as const) {
      await expect(player1.page.getByRole('heading', {
        name: `Dredge ${amount} — Mill ${amount} cards and return ${name} from your graveyard to your hand instead of drawing?`,
        exact: true,
      })).toBeVisible()
      await p1.answerNo()
    }

    await expect(player1.page.getByRole('heading', {
      name: 'Dredge 1 — Mill a card and return Grave-Shell Scarab from your graveyard to your hand instead of drawing?',
      exact: true,
    })).toBeVisible()
    if (acceptScarab) {
      const screenshot = testInfo.outputPath('dredge-choice.png')
      await player1.page.screenshot({ path: screenshot })
      await testInfo.attach('Dredge choice', { path: screenshot, contentType: 'image/png' })
      await p1.answerYes()
      await p1.expectInHand('Grave-Shell Scarab')
      await p1.expectHandSize(1)
    } else {
      await p1.answerNo()
      await p1.expectInHand('Forest')
      await p1.expectNotInHand('Grave-Shell Scarab')
      await p1.expectHandSize(1)
    }
    await expect(player1.page.getByRole('heading', { name: /^Dredge / })).toHaveCount(0)
  })
}
