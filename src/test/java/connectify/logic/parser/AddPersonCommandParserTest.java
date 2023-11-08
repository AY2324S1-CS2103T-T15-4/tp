package connectify.logic.parser;

import static connectify.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static connectify.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static connectify.logic.parser.CliSyntax.PREFIX_COMPANY;
import static connectify.logic.parser.CliSyntax.PREFIX_EMAIL;
import static connectify.logic.parser.CliSyntax.PREFIX_NAME;
import static connectify.logic.parser.CliSyntax.PREFIX_PHONE;
import static connectify.logic.parser.CliSyntax.PREFIX_PRIORITY;
import static connectify.testutil.TypicalIndexes.INDEX_FIRST_COMPANY;
import static connectify.testutil.TypicalPersons.AMY;
import static connectify.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import connectify.logic.Messages;
import connectify.logic.commands.AddPersonCommand;
import connectify.logic.commands.CommandTestUtil;
import connectify.model.person.Person;
import connectify.model.person.PersonAddress;
import connectify.model.person.PersonEmail;
import connectify.model.person.PersonName;
import connectify.model.person.PersonPhone;
import connectify.model.tag.Tag;
import connectify.testutil.PersonBuilder;

public class AddPersonCommandParserTest {
    private final AddPersonCommandParser parser = new AddPersonCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags(CommandTestUtil.VALID_TAG_FRIEND).build();

        // whitespace only preamble
        CommandParserTestUtil.assertParseSuccess(parser, CommandTestUtil.PREAMBLE_WHITESPACE
                    + CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB + CommandTestUtil.EMAIL_DESC_BOB
                    + CommandTestUtil.ADDRESS_DESC_BOB + CommandTestUtil.TAG_DESC_FRIEND
                    + CommandTestUtil.COMPANY_DESC_BOB + CommandTestUtil.PRIORITY_DESC_BOB,
                    new AddPersonCommand(expectedPerson, INDEX_FIRST_COMPANY));


        // multiple tags - all accepted
        Person expectedPersonMultipleTags = new PersonBuilder(BOB).withTags(CommandTestUtil.VALID_TAG_FRIEND,
                CommandTestUtil.VALID_TAG_HUSBAND).build();
        CommandParserTestUtil.assertParseSuccess(parser,
            CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB + CommandTestUtil.EMAIL_DESC_BOB
                    + CommandTestUtil.ADDRESS_DESC_BOB + CommandTestUtil.TAG_DESC_HUSBAND
                    + CommandTestUtil.TAG_DESC_FRIEND + CommandTestUtil.COMPANY_DESC_BOB
                    + CommandTestUtil.PRIORITY_DESC_BOB,
                new AddPersonCommand(expectedPersonMultipleTags, INDEX_FIRST_COMPANY));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedPersonString = CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB
                    + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.ADDRESS_DESC_BOB
                    + CommandTestUtil.TAG_DESC_FRIEND + CommandTestUtil.COMPANY_DESC_BOB
                    + CommandTestUtil.PRIORITY_DESC_BOB;

        // multiple names
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.NAME_DESC_AMY
                    + validExpectedPersonString, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple phones
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.PHONE_DESC_AMY
                    + validExpectedPersonString, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple emails
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.EMAIL_DESC_AMY
                    + validExpectedPersonString, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // multiple addresses
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.ADDRESS_DESC_AMY
                    + validExpectedPersonString, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // multiple fields repeated
        CommandParserTestUtil.assertParseFailure(parser,
            validExpectedPersonString + CommandTestUtil.PHONE_DESC_AMY + CommandTestUtil.EMAIL_DESC_AMY
                    + CommandTestUtil.NAME_DESC_AMY + CommandTestUtil.ADDRESS_DESC_AMY + validExpectedPersonString,
                    Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_ADDRESS,
                            PREFIX_EMAIL, PREFIX_PHONE, PREFIX_COMPANY, PREFIX_PRIORITY));

        // invalid value followed by valid value

        // invalid name
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.INVALID_NAME_DESC
                    + validExpectedPersonString, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.INVALID_EMAIL_DESC
                    + validExpectedPersonString, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.INVALID_PHONE_DESC
                    + validExpectedPersonString, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.INVALID_ADDRESS_DESC
                    + validExpectedPersonString, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // valid value followed by invalid value

        // invalid name
        CommandParserTestUtil.assertParseFailure(parser, validExpectedPersonString
                    + CommandTestUtil.INVALID_NAME_DESC, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        CommandParserTestUtil.assertParseFailure(parser, validExpectedPersonString
                    + CommandTestUtil.INVALID_EMAIL_DESC, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        CommandParserTestUtil.assertParseFailure(parser, validExpectedPersonString
                    + CommandTestUtil.INVALID_PHONE_DESC, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        CommandParserTestUtil.assertParseFailure(parser, validExpectedPersonString
                    + CommandTestUtil.INVALID_ADDRESS_DESC,
                    Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        CommandParserTestUtil.assertParseSuccess(parser, CommandTestUtil.NAME_DESC_AMY
                    + CommandTestUtil.PHONE_DESC_AMY + CommandTestUtil.EMAIL_DESC_AMY
                    + CommandTestUtil.ADDRESS_DESC_AMY + CommandTestUtil.COMPANY_DESC_AMY
                    + CommandTestUtil.PRIORITY_DESC_AMY,
                new AddPersonCommand(expectedPerson, INDEX_FIRST_COMPANY));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddPersonCommand.MESSAGE_USAGE);

        // missing name prefix
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.VALID_NAME_BOB
                    + CommandTestUtil.PHONE_DESC_BOB + CommandTestUtil.EMAIL_DESC_BOB
                    + CommandTestUtil.ADDRESS_DESC_BOB, expectedMessage);

        // missing phone prefix
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.NAME_DESC_BOB
                    + CommandTestUtil.VALID_PHONE_BOB + CommandTestUtil.EMAIL_DESC_BOB
                    + CommandTestUtil.ADDRESS_DESC_BOB, expectedMessage);

        // missing email prefix
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.NAME_DESC_BOB
                    + CommandTestUtil.PHONE_DESC_BOB + CommandTestUtil.VALID_EMAIL_BOB
                    + CommandTestUtil.ADDRESS_DESC_BOB, expectedMessage);

        // missing address prefix
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.NAME_DESC_BOB
                    + CommandTestUtil.PHONE_DESC_BOB + CommandTestUtil.EMAIL_DESC_BOB
                    + CommandTestUtil.VALID_ADDRESS_BOB, expectedMessage);

        // all prefixes missing
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.VALID_NAME_BOB
                    + CommandTestUtil.VALID_PHONE_BOB + CommandTestUtil.VALID_EMAIL_BOB
                    + CommandTestUtil.VALID_ADDRESS_BOB, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.INVALID_NAME_DESC
                + CommandTestUtil.PHONE_DESC_BOB + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.ADDRESS_DESC_BOB
                + CommandTestUtil.TAG_DESC_HUSBAND + CommandTestUtil.TAG_DESC_FRIEND
                + CommandTestUtil.PRIORITY_DESC_BOB + CommandTestUtil.COMPANY_DESC_BOB,
                PersonName.MESSAGE_CONSTRAINTS);

        // invalid phone
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.NAME_DESC_BOB
                + CommandTestUtil.INVALID_PHONE_DESC + CommandTestUtil.EMAIL_DESC_BOB
                + CommandTestUtil.ADDRESS_DESC_BOB
                + CommandTestUtil.TAG_DESC_HUSBAND + CommandTestUtil.TAG_DESC_FRIEND
                + CommandTestUtil.PRIORITY_DESC_BOB + CommandTestUtil.COMPANY_DESC_BOB,
                PersonPhone.MESSAGE_CONSTRAINTS);

        // invalid email
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.NAME_DESC_BOB
                + CommandTestUtil.PHONE_DESC_BOB + CommandTestUtil.INVALID_EMAIL_DESC
                + CommandTestUtil.ADDRESS_DESC_BOB
                + CommandTestUtil.TAG_DESC_HUSBAND + CommandTestUtil.TAG_DESC_FRIEND
                + CommandTestUtil.PRIORITY_DESC_BOB + CommandTestUtil.COMPANY_DESC_BOB,
                PersonEmail.MESSAGE_CONSTRAINTS);

        // invalid address
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.NAME_DESC_BOB
                + CommandTestUtil.PHONE_DESC_BOB + CommandTestUtil.EMAIL_DESC_BOB
                + CommandTestUtil.INVALID_ADDRESS_DESC
                + CommandTestUtil.TAG_DESC_HUSBAND + CommandTestUtil.TAG_DESC_FRIEND
                + CommandTestUtil.PRIORITY_DESC_BOB + CommandTestUtil.COMPANY_DESC_BOB,
                PersonAddress.MESSAGE_CONSTRAINTS);

        // invalid tag
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.NAME_DESC_BOB
                + CommandTestUtil.PHONE_DESC_BOB + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.ADDRESS_DESC_BOB
                + CommandTestUtil.INVALID_TAG_DESC + CommandTestUtil.VALID_TAG_FRIEND
                + CommandTestUtil.PRIORITY_DESC_BOB + CommandTestUtil.COMPANY_DESC_BOB,
                Tag.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.INVALID_NAME_DESC
                + CommandTestUtil.PHONE_DESC_BOB + CommandTestUtil.EMAIL_DESC_BOB
                + CommandTestUtil.INVALID_ADDRESS_DESC
                + CommandTestUtil.PRIORITY_DESC_BOB + CommandTestUtil.COMPANY_DESC_BOB,
                PersonName.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        CommandParserTestUtil.assertParseFailure(parser, CommandTestUtil.PREAMBLE_NON_EMPTY
                + CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB
                + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.ADDRESS_DESC_BOB
                + CommandTestUtil.TAG_DESC_HUSBAND + CommandTestUtil.TAG_DESC_FRIEND
                + CommandTestUtil.PRIORITY_DESC_BOB + CommandTestUtil.COMPANY_DESC_BOB,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddPersonCommand.MESSAGE_USAGE));
    }
}
