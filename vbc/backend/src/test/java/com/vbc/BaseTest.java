package com.vbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected static ResultMatcher success() {
        return result -> {
            status().isOk().match(result);
            jsonPath("$.code").value(200).match(result);
        };
    }

    protected static ResultMatcher error() {
        return jsonPath("$.code").isNumber();
    }

    @SuppressWarnings("unchecked")
    protected <T> T parseData(MvcResult result, Class<T> clazz) throws Exception {
        String json = result.getResponse().getContentAsString();
        var node = objectMapper.readTree(json);
        return objectMapper.treeToValue(node.get("data"), clazz);
    }
}
