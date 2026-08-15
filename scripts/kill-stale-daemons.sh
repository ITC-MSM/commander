#!/usr/bin/env bash
#
# kill-stale-daemons.sh — reap Kotlin compile daemons and Gradle daemons that have been idle
# long enough that nothing is going to reuse them.
#
# Why: `org.gradle.daemon.idletimeout` only governs the *Gradle* daemon. The Kotlin compile
# daemon is a separate JVM with its own (larger, now 6g) heap and its own lifetime, and on a box
# running many worktree agents these accumulate: several hours-old, 0%-CPU JVMs each holding
# gigabytes, which is what pushes the machine into swap and makes the *next* compile OOM.
#
# Idle is detected by sampling cumulative CPU time twice: a daemon that burns no CPU across the
# sample window and has been up longer than --min-age is not mid-compile.
#
# SAFETY:
#   * refuses to run while any gradle-locked semaphore slot is held — a build is in flight and its
#     daemon may legitimately sit at 0% CPU while tests run in a forked JVM;
#   * never touches `kotlin-lsp` / language-server JVMs (those are the user's editor, not ours);
#   * DRY RUN unless --apply is passed.
#
# Usage:
#   scripts/kill-stale-daemons.sh                        # dry run, 60-minute threshold
#   scripts/kill-stale-daemons.sh --min-age 30 --apply

set -euo pipefail

MIN_AGE_MIN=60
SAMPLE_SECONDS=3
APPLY=0
LOCK_FILE="${GRADLE_LOCK_FILE:-$HOME/.cache/argentum/gradle.lock}"

while [ $# -gt 0 ]; do
    case "$1" in
        --min-age) MIN_AGE_MIN="$2"; shift 2 ;;
        --apply) APPLY=1; shift ;;
        --dry-run) APPLY=0; shift ;;
        -h|--help) sed -n '2,24p' "$0"; exit 0 ;;
        *) echo "kill-stale-daemons: unknown argument '$1'" >&2; exit 2 ;;
    esac
done

if ! [[ "$MIN_AGE_MIN" =~ ^[0-9]+$ ]]; then
    echo "kill-stale-daemons: --min-age must be an integer (got '$MIN_AGE_MIN')." >&2
    exit 2
fi
MIN_AGE_SEC=$((MIN_AGE_MIN * 60))

for slot in "$LOCK_FILE" "$LOCK_FILE".[0-9]; do
    if [ -e "$slot" ]; then
        echo "kill-stale-daemons: '$slot' is held — a Gradle build is running. Not reaping." >&2
        exit 0
    fi
done

# pid<TAB>elapsed-seconds<TAB>rss-kb<TAB>cpu-seconds<TAB>label
#
# macOS `ps` has no `etimes`, only the formatted `etime` ([[dd-]hh:]mm:ss), and `time` is
# mm:ss.ss or hh:mm:ss — so both are parsed by hand rather than read as integers.
snapshot() {
    ps -Ao pid=,etime=,rss=,time=,command= | awk -v OFS='\t' '
        function secs(s,   days, parts, n, i, out) {
            days = 0
            if (index(s, "-") > 0) { days = substr(s, 1, index(s, "-") - 1) + 0
                                     s = substr(s, index(s, "-") + 1) }
            n = split(s, parts, ":")
            out = 0
            for (i = 1; i <= n; i++) out = out * 60 + (parts[i] + 0)
            return int(out + days * 86400)
        }
        /kotlin-lsp|language-server/ { next }
        /KotlinCompileDaemon/        { label = "kotlin-daemon" }
        /GradleDaemon/               { label = "gradle-daemon" }
        label != "" {
            print $1, secs($2), $3, secs($4), label
            label = ""
        }'
}

before="$(snapshot)"
if [ -z "$before" ]; then
    echo "kill-stale-daemons: no Gradle or Kotlin daemons running."
    exit 0
fi

sleep "$SAMPLE_SECONDS"
after="$(snapshot)"

reclaimed_kb=0
while IFS=$'\t' read -r pid etimes rss cpu label; do
    [ -n "${pid:-}" ] || continue
    cpu_before=$(printf '%s\n' "$before" | awk -F'\t' -v p="$pid" '$1 == p {print $4}')
    [ -n "$cpu_before" ] || continue

    rss_h=$(awk -v k="$rss" 'BEGIN{printf "%.1f GB", k/1024/1024}')
    age_h=$(awk -v s="$etimes" 'BEGIN{printf "%dh%02dm", s/3600, (s%3600)/60}')

    if [ "$etimes" -lt "$MIN_AGE_SEC" ]; then
        echo "keep  $label pid=$pid age=$age_h rss=$rss_h  (younger than ${MIN_AGE_MIN}m)"
        continue
    fi
    if [ "$cpu" -ne "$cpu_before" ]; then
        echo "keep  $label pid=$pid age=$age_h rss=$rss_h  (burned CPU during the ${SAMPLE_SECONDS}s sample)"
        continue
    fi

    reclaimed_kb=$((reclaimed_kb + rss))
    if [ "$APPLY" -eq 1 ]; then
        echo "kill  $label pid=$pid age=$age_h rss=$rss_h"
        kill "$pid" 2>/dev/null || true
    else
        echo "would $label pid=$pid age=$age_h rss=$rss_h"
    fi
done <<< "$after"

echo
awk -v k="$reclaimed_kb" -v a="$APPLY" 'BEGIN{
    printf "%s: %.1f GB resident\n", (a ? "reclaimed" : "reclaimable (re-run with --apply)"), k/1024/1024
}'
