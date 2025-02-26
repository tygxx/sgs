package com.sgs.web.controller.busi;

import com.sgs.busi.model.SgsFileInfo;
import com.sgs.busi.utils.SgsFileParserUtils;
import com.sgs.common.annotation.Anonymous;
import com.sgs.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Author: tengYong
 * @date: 2025/2/25
 * @description:
 */
@Tag(name = "SGS管理")
@RestController
@RequestMapping("/busi/sgs")
public class BusiSgsController {

    @Anonymous
    @Operation(summary = "读取文档内容")
    @PostMapping("/read-document")
    public R<SgsFileInfo> readDocument(@RequestParam String filePath) throws IOException {
        return R.ok(SgsFileParserUtils.parseSgsFile(filePath));
    }
}
