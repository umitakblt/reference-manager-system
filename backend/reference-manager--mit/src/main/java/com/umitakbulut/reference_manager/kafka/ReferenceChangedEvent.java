package com.umitakbulut.reference_manager.kafka;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceChangedEvent {

    private String entity;
    private Long entityId;
    private String operation;
    private String timestamp;
}
