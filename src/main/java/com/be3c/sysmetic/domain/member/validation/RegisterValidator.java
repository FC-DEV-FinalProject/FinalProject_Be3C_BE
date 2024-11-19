package com.be3c.sysmetic.domain.member.validation;

import com.be3c.sysmetic.domain.member.dto.RegisterRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.regex.Pattern;

@Component
public class RegisterValidator implements Validator {

    private static final Pattern IMAGE_PATTERN = Pattern.compile("^.+\\.(jpg|jpeg|png|gif)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,20}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[가-힣]{1,10}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣0-9]{3,10}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^010\\d{8}$");

    // 검증하려는 클래스 체크
    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterRequestDto.class.equals(clazz);
    }

    // 검증
    @Override
    public void validate(Object target, Errors errors) {
        RegisterRequestDto dto = (RegisterRequestDto) target;

        /*
            [전제조건]
             - 프로필 이미지를 제외한 모든 항목은 NotNull & NotBlank 이어야 한다.

            [검증 항목]
            1. 프로필 이미지 - jpg, jpeg, png, gif
            2. 회원 등급 코드
            3. 이메일 - ^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$
            4. 비밀번호 - 영문자(대,소문자), 숫자, 특수문자 포함 / 6~20자
            5. 비밀번호 재입력 - 영문자(대,소문자), 숫자, 특수문자 포함 / 6~20자
            6. 이름 - 한글 / 10자 이내
            7. 닉네임 - 한글 또는 숫자 포함 / 3~10자
            8. 휴대폰번호 - 010 을 포함한 숫자
            9. 정보성 수신 동의 여부
            10. 정보성 수신 동의일
            11. 마케팅 수신 동의 여부
            12. 마케팅 수신 동의일
         */

        // 1. 프로필 이미지 형식 확인
        if (dto.getProfileImage() != null && !isValidImage(dto.getProfileImage())) {
            errors.rejectValue("profileImage", "Invalid.profileImage", "프로필 이미지는 jpg, jpeg, png, gif 형식의 파일만 허용됩니다.");
        }

        // 2. 회원 등급 코드 NotNull/NotBlank 확인
        if (isNullOrBlank(dto.getRoleCode())) {
            errors.rejectValue("roleCode", "NotEmpty.roleCode", "회원 유형 선택은 필수입니다. 가입하려는 회원 유형을 선택해 주세요.");
        }

        // 3. 이메일 NotNull/NotBlank 및 형식 확인
        if (isNullOrBlank(dto.getEmail())) {
            System.out.println("[Validator 메시지]");
            errors.rejectValue("email", "NotEmpty.email", "이메일 입력은 필수입니다.");
        } else if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
            System.out.println("[Validator 메시지]");
            errors.rejectValue("email", "Invalid.email", "유효한 이메일 형식이 아닙니다.");
        }

        // 4. 비밀번호 NotNull/NotBlank 및 형식 확인
        if (isNullOrBlank(dto.getPassword())) {
            errors.rejectValue("password", "NotEmpty.password", "비밀번호 입력은 필수입니다.");
        } else if (!PASSWORD_PATTERN.matcher(dto.getPassword()).matches()) {
            errors.rejectValue("password", "Invalid.password", "비밀번호는 영문자(대, 소문자), 숫자, 특수문자를 포함하여 6~20자로 입력해야 합니다.");
        }

        // 5. 비밀번호 재입력 확인
        if (!dto.getPassword().equals(dto.getRewritePassword())) {
            errors.rejectValue("rewritePassword", "Mismatch.passwordConfirm", "비밀번호가 일치하지 않습니다.");
        }

        // 6. 이름 NotNull/NotBlank 및 형식 확인
        if (isNullOrBlank(dto.getName())) {
            errors.rejectValue("name", "NotEmpty.name", "이름 입력은 필수입니다.");
        } else if (!NAME_PATTERN.matcher(dto.getName()).matches()) {
            errors.rejectValue("name", "Invalid.name", "이름은 한글 1자 이상 10자 이내로 입력해야 합니다.");
        }

        // 7. 닉네임 NotNull/NotBlank 및 형식 확인
        if (isNullOrBlank(dto.getNickname())) {
            errors.rejectValue("nickname", "NotEmpty.nickname", "닉네임 입력은 필수입니다.");
        } else if (!NICKNAME_PATTERN.matcher(dto.getNickname()).matches()) {
            errors.rejectValue("nickname", "Invalid.nickname", "닉네임은 한글 또는 숫자로 3자 이상 10자 이내로 입력해야 합니다.");
        }

        // 0. 생년월일 형식 확인
        if (dto.getBirth() == null) {
            errors.rejectValue("birth", "NotNull.birth", "생년월일은 필수 입력 값입니다.");
        } else if (!isValidAge(LocalDateTime.parse(dto.getBirth()))) {
            errors.rejectValue("birth", "Invalid.birth", "14세 미만은 가입할 수 없습니다.");
        }

        // 8. 휴대폰 번호 NotNull/NotBlank 및 형식 확인
        if (isNullOrBlank(dto.getPhoneNumber())) {
            errors.rejectValue("phoneNumber", "NotEmpty.phoneNumber", "휴대폰번호 입력은 필수입니다.");
        } else if (!PHONE_PATTERN.matcher(dto.getPhoneNumber()).matches()) {
            errors.rejectValue("phoneNumber", "Invalid.phoneNumber", "휴대폰번호는 - 없이 010으로 시작하는 11자리 숫자로 입력해야 합니다.");
        }

        // 9. 정보성 수신 동의 여부 확인
        if (isNullOrBlank(String.valueOf(dto.getReceiveInfoConsent())) || (!dto.getReceiveInfoConsent())) {
            errors.rejectValue("receiveInfoConsent", "NotNull.receiveInfoConsent", "정보성 수신 동의 여부를 선택해 주세요.");
        }

        // 10. 정보성 수신 동의일 형식 확인
        if (dto.getInfoConsentDate() == null) {
            errors.rejectValue("infoConsentDate", "NotNull.infoConsentDate", "정보성 수신 동의일은 필수 입력 값입니다.");
        }

        // 11. 마케팅 수신 동의 여부 확인
        if (isNullOrBlank(String.valueOf(dto.getReceiveMarketingConsent())) || (!dto.getReceiveMarketingConsent())) {
            errors.rejectValue("receiveMarketingConsent", "NotNull.receiveMarketingConsent", "마케팅 수신 동의 여부를 선택해 주세요.");
        }

        // 12. 마케팅 수신 동의일 형식 확인
        if (dto.getMarketingConsentDate() == null) {
            errors.rejectValue("marketingConsentDate", "NotNull.marketingConsentDate", "마케팅 수신 동의일은 필수 입력 값입니다.");
        }
    }

    private boolean isValidImage(MultipartFile file) {
        return file != null && !file.isEmpty() && IMAGE_PATTERN.matcher(file.getOriginalFilename()).matches();
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidAge(LocalDateTime birthDateTime) {
        LocalDate birthDate = birthDateTime.toLocalDate();
        LocalDate today = LocalDate.now();

        // 나이 계산
        int age = Period.between(birthDate, today).getYears();

        // 14세 이상인지 확인
        return age >= 14;
    }

}
