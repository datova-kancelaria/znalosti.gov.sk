package sk.gov.knowledgegraph.model.exception;

import lombok.Getter;

@Getter
public enum ErrorCode implements Error {

    UNEXPECTED_FILLED_URI_ATTRIBUTE_IN_CREATION_OF_APPLICATION("Unexpected filled URI attribute in creation of application!", 400), 
    NO_APPLICATION_FOUND("No application found!", 400),
    INTERNAL_DATA_CORRUPTION_TOO_MANY_APPLICATIONS_FOUND_FOR_URI("Too many applications found for URI!", 400),
    DATATYPE_PROPERTY_COMPONENT_IS_ALREADY_APPROVED("Dataproperty is already approved!", 400),
    NOT_VALID("Not valid!", 400),
    UNKNOWN_REPOSITORY("Unknown repository!", 400),
    DB_RESET_NOT_ALLOWED("DB reset not allowed!", 400),
    BRANCH_TO_RELOAD_DOES_NOT_EXIST("Branch to reload does not exist!", 400),
    YOU_CAN_NOT_DELETE_APPLICATION_WHICH_IS_NOT_IN_DRAFT_STATE("You can not delete application which is not in draft state!", 400),
    YOU_CAN_NOT_REJECT_APPLICATION_WHICH_IS_NOT_IN_APPLIED_STATE("You can not reject application which is not in applied state!", 400),
    YOU_CAN_NOT_APPROVE_APPLICATION_WHICH_IS_NOT_IN_DRAFT_STATE("You can not approve application which is not in draft state!", 400),
    REPOSITORY_DOES_NOT_EXIST("Repository does not exist!", 400),
    TECHNICAL_ERROR("Technical error!", 500), 
    OUTPUT_FORMAT_FORMAT_MISSING("Missing output format!", 400), 
    MISMATCHED_ACCEPT_HEADER("Mismatched accept header!", 400);

    private final String message;

    private final int code;

    ErrorCode(final String message, final int code) {
        this.message = message;
        this.code = code;
    }

}
