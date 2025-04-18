package com.paymybuddy.controller.api;

import com.paymybuddy.entity.Role;
import com.paymybuddy.entity.User;
import com.paymybuddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final UserService userService;

    // Afficher les utilisateurs non-amis pour ajout potentiel
    @GetMapping("/add")
    public String showPotentialFriends(Model model, Authentication auth) {
        String email = auth.getName();

        User currentUser = userService.findByEmailWithFriends(email);
        List<User> potentialFriends = userService.getUsersNotAlreadyFriends(email)

         .stream()
                .filter(user -> user.getRole() == Role.ROLE_USER)
                .collect(Collectors.toList());


        model.addAttribute("potentialFriends", potentialFriends);
        model.addAttribute("user", currentUser);

        return "add-friend";
    }

    // Traitement de l'ajout d'amis
    @PostMapping("/add")
    @Transactional
    public String addFriend(@RequestParam("friendId") Integer friendId, Authentication auth) {
        User currentUser = userService.findByEmailWithFriends(auth.getName());
        User friend = userService.findById(friendId);

        currentUser.getFriends().add(friend);
        friend.getFriends().add(currentUser);

        userService.updateUser(currentUser);
        userService.updateUser(friend);

        return "redirect:/dashboard";
    }
    @PostMapping("/add-new")
    @Transactional
    public String addFriendByEmail(@RequestParam String email, Authentication auth, RedirectAttributes redirectAttrs) {
        User currentUser = userService.findByEmailWithFriends(auth.getName());

        // Vérifie l'existence de l'utilisateur dans la base de données
        Optional<User> existingUserOpt = userService.findByEmail(email);

        if (existingUserOpt.isEmpty()) {
            // utilisateur non trouvé, donc impossible de l'ajouter
            redirectAttrs.addFlashAttribute("error", "Aucun utilisateur avec cet email n'existe.");
            return "redirect:/friends/add";
        }

        User friend = existingUserOpt.get();

        // Vérifie si l'utilisateur n'est pas déjà ami
        if (!currentUser.getFriends().contains(friend)) {
            currentUser.getFriends().add(friend);
            friend.getFriends().add(currentUser);

            userService.updateUser(currentUser);
            userService.updateUser(friend);
            redirectAttrs.addFlashAttribute("success", "Ami ajouté avec succès !");
        } else {
            redirectAttrs.addFlashAttribute("error", "Vous êtes déjà ami avec cet utilisateur.");
        }

        return "redirect:/friends/add";
    }


}

