package com.server.cli;

import com.tamasenco.gameserver.utilities.json.VertxSerializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public final class CliCommandRequest implements VertxSerializable {
    private CliCommand command;
    private List<String> arguments;
}
