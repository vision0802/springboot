package org.vision.github.springboot.mqserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
@AllArgsConstructor @NoArgsConstructor
@Data public class ResultMsg {
    private String code;
    private String msg;
}