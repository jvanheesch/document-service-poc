package com.github.jvanheesch.document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Document {
    @Setter(AccessLevel.NONE)
    private Long id;
    private String name;
    private DocumentStatus status;
}
