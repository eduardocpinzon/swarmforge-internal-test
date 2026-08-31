# Name registration visual-feedback end-to-end QA suite

Run these checks through the delivered user interface. Do not call a project API,
inspect implementation state, or trigger visual effects through developer tools.

## Preconditions

Start with a fresh local user-data state and open the name registration screen.

## QA-1: Observe the create feedback

1. Enter `Ana` in the new-name input and submit it.
2. Observe the resulting list item as it appears.
3. Verify that `Ana` remains visible after a clear arrival transition.

## QA-2: Observe the edit feedback

1. Edit `Ana` to `Beatriz` and save the edit.
2. Verify that `Beatriz` replaces `Ana`.
3. Verify that the updated `Beatriz` item receives a visible, temporary highlight.

## QA-3: Observe the delete feedback

1. Add `Bruno` through the screen.
2. Delete `Beatriz` through its delete control.
3. Verify that the `Beatriz` item visibly transitions out before it is absent.
4. Verify that `Bruno` remains visible throughout the action.

## QA-4: Use the screen with reduced motion

1. Enable the user environment's reduced-motion setting through its normal user-facing controls.
2. Reload the name registration screen, edit `Bruno` to `Caio`, and save it.
3. Verify that `Caio` replaces `Bruno` with clear feedback and no nonessential movement.
