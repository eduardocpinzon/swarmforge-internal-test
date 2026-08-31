# Local name registration end-to-end QA suite

Run these checks through the delivered user interface. Do not call a project API,
inspect application storage, or seed data outside the interface.

## Preconditions

Start with a fresh local user-data state, then open the name registration screen.

## QA-1: Add a name

1. Type `Ana` into the new-name input and submit it.
2. Verify that `Ana` appears in the registered-name list.
3. Verify that the new-name input is empty and ready for another entry.

## QA-2: Edit a saved name

1. Use the edit control for `Ana`.
2. Replace its value with `Beatriz` and save the change.
3. Verify that `Beatriz` is visible in the list and `Ana` is absent.

## QA-3: Delete one saved name without affecting another

1. Add `Bruno` through the screen.
2. Delete `Beatriz` through its delete control.
3. Verify that `Beatriz` is absent and `Bruno` remains visible.

## QA-4: Retain a saved name locally

1. Reload or close and reopen the name registration screen through the normal user interface.
2. Verify that `Bruno` is still visible and `Beatriz` remains absent.

## QA-5: Reject an empty name

1. Submit the new-name input without entering a value.
2. Verify a visible message says that a name is required.
3. Verify that no blank list item is added and `Bruno` remains visible.
