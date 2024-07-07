package tech.biblio.BookListing.entities;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Privilege {
    CREATE_POST("create:post"),
    CREATE_COMMENT("create:comment"),
    CREATE_USER("create:user"),

    READ_COMMENT("read:comment"),
    READ_POST("read:post"),
    READ_USER("read:user"),

    DELETE_POST("delete:post"),
    DELETE_COMMENT("delete:comment"),
    DELETE_USER("delete:user"),

    UPDATE_POST("update:post"),
    UPDATE_COMMENT("update:comment"),
    UPDATE_USER("update:user")
    ;

    @Getter
    private final String privilege;
}
