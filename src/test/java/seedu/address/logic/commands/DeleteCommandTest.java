package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.model.AddressBookModel;
import seedu.address.model.AddressBookModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the AddressBookModel, UndoCommand and RedoCommand) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private AddressBookModel addressBookModel = new AddressBookModelManager(getTypicalAddressBook(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = addressBookModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, personToDelete);

        AddressBookModelManager expectedModel = new AddressBookModelManager(addressBookModel.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        expectedModel.commitAddressBook();

        assertCommandSuccess(deleteCommand, addressBookModel, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(addressBookModel.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, addressBookModel, commandHistory, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(addressBookModel, INDEX_FIRST_PERSON);

        Person personToDelete = addressBookModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, personToDelete);

        AddressBookModel expectedAddressBookModel = new AddressBookModelManager(addressBookModel.getAddressBook(), new UserPrefs());
        expectedAddressBookModel.deletePerson(personToDelete);
        expectedAddressBookModel.commitAddressBook();
        showNoPerson(expectedAddressBookModel);

        assertCommandSuccess(deleteCommand, addressBookModel, commandHistory, expectedMessage, expectedAddressBookModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(addressBookModel, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < addressBookModel.getAddressBook().getAllPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, addressBookModel, commandHistory, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndoRedo_validIndexUnfilteredList_success() throws Exception {
        Person personToDelete = addressBookModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        AddressBookModel expectedAddressBookModel = new AddressBookModelManager(addressBookModel.getAddressBook(), new UserPrefs());
        expectedAddressBookModel.deletePerson(personToDelete);
        expectedAddressBookModel.commitAddressBook();

        // delete -> first person deleted
        deleteCommand.execute(addressBookModel, commandHistory);

        // undo -> reverts addressbook back to previous state and filtered person list to show all persons
        expectedAddressBookModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), addressBookModel, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedAddressBookModel);

        // redo -> same first person deleted again
        expectedAddressBookModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), addressBookModel, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedAddressBookModel);
    }

    @Test
    public void executeUndoRedo_invalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(addressBookModel.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        // execution failed -> address book state not added into addressBookModel
        assertCommandFailure(deleteCommand, addressBookModel, commandHistory, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        // single address book state in addressBookModel -> undoCommand and redoCommand fail
        assertCommandFailure(new UndoCommand(), addressBookModel, commandHistory, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(new RedoCommand(), addressBookModel, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }

    /**
     * 1. Deletes a {@code Person} from a filtered list.
     * 2. Undo the deletion.
     * 3. The unfiltered list should be shown now. Verify that the index of the previously deleted person in the
     * unfiltered list is different from the index at the filtered list.
     * 4. Redo the deletion. This ensures {@code RedoCommand} deletes the person object regardless of indexing.
     */
    @Test
    public void executeUndoRedo_validIndexFilteredList_samePersonDeleted() throws Exception {
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        AddressBookModel expectedAddressBookModel = new AddressBookModelManager(addressBookModel.getAddressBook(), new UserPrefs());

        showPersonAtIndex(addressBookModel, INDEX_SECOND_PERSON);
        Person personToDelete = addressBookModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        expectedAddressBookModel.deletePerson(personToDelete);
        expectedAddressBookModel.commitAddressBook();

        // delete -> deletes second person in unfiltered person list / first person in filtered person list
        deleteCommand.execute(addressBookModel, commandHistory);

        // undo -> reverts addressbook back to previous state and filtered person list to show all persons
        expectedAddressBookModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), addressBookModel, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedAddressBookModel);

        assertNotEquals(personToDelete, addressBookModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()));
        // redo -> deletes same second person in unfiltered person list
        expectedAddressBookModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), addressBookModel, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedAddressBookModel);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    /**
     * Updates {@code addressBookModel}'s filtered list to show no one.
     */
    private void showNoPerson(AddressBookModel addressBookModel) {
        addressBookModel.updateFilteredPersonList(p -> false);

        assertTrue(addressBookModel.getFilteredPersonList().isEmpty());
    }
}
