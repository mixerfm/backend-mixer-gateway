package fm.mixer.gateway.api.user.mapper;

import fm.mixer.gateway.api.v1.model.TestData;
import fm.mixer.gateway.client.model.TestDataDto;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    TestData mapToTestData(TestDataDto testDataDto);
}
