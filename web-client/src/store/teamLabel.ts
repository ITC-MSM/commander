/**
 * The name to print for a team: "Your Team" / "Opponents" from the viewer's seat, and the
 * neutral "Team N" when there is no viewer team to be relative to (spectator, replay). Same
 * vocabulary the rail's two team sections already use, so the center HUD and the rail agree.
 *
 * Its own module rather than a member of `selectors.ts`: it reads no store, and keeping it clear
 * of the store's import graph is what lets it be tested without a DOM.
 */
export function teamLabel(teamIndex: number | null, viewerTeam: number | null): string {
  if (teamIndex == null) return ''
  if (viewerTeam == null) return `Team ${teamIndex + 1}`
  return teamIndex === viewerTeam ? 'Your Team' : 'Opponents'
}
