package sk.gov.knowledgegraph.model.expcetion;

public interface Error {

    String getMessage();


    String name();


    int getCode(); //HttpStatus.code

}
