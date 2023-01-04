package com.nagesoft.club.modules.account.form;

import lombok.Data;

//@NoArgsConstructor
@Data
public class NotificationForm {
    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;

//    public NotificationForm(Account account) {
//        this.studyCreatedByEmail = account.isStudyCreatedByEmail();
//        this.studyCreatedByWeb = account.isStudyCreatedByWeb();
//        this.studyEnrollmentResultByEmail = account.isStudyEnrollmentResultByEmail();
//        this.studyEnrollmentResultByWeb = account.isStudyEnrollmentResultByWeb();
//        this.studyUpdatedByEmail = account.isStudyUpdatedByEmail();
//        this.studyUpdatedByWeb = account.isStudyUpdatedByWeb();
//    }
}
