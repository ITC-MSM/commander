/** Truthful release-readiness gate.  This is deliberately not Set Coverage. */
export interface CardReadiness {
  readonly schemaVersion: number
  readonly toolVersion: string
  readonly generatedFrom: {
    readonly commit: string
    readonly commitTimestamp: string
    readonly catalogSource: string
    readonly catalogSha256: string
    readonly ledgerSource: string
    readonly ledgerSha256: string
    readonly triageLedgerSource: string
    readonly triageLedgerSha256: string | null
  }
  readonly semantics: {
    readonly catalogCoverage: string
    readonly verified: string
    readonly releaseEligible: boolean
    readonly releaseEligibleReason: string
  }
  readonly counts: Readonly<Record<string, number>>
  readonly samples: Readonly<Record<string, readonly { name: string; sets: readonly string[]; blocker?: string | null }[]>>
}

export async function fetchCardReadiness(): Promise<CardReadiness> {
  const response = await fetch('/api/readiness')
  if (!response.ok) throw new Error(`Failed to load readiness (${response.status})`)
  return response.json() as Promise<CardReadiness>
}
