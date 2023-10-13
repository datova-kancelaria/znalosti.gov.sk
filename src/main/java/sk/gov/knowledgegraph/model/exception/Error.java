package sk.gov.knowledgegraph.model.exception;

public interface Error {

    String getMessage();


    String name();


    int getCode(); //HttpStatus.code

}
