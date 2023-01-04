package com.nagesoft.club.modules.account;

import com.nagesoft.club.modules.tag.Tag;
import com.nagesoft.club.modules.zone.Zone;
import lombok.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.persistence.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account ")
public @interface CurrentAccount {
}
