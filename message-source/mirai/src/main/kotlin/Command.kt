package org.meowcat.mesagisto.mirai

import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.MemberCommandSender
import net.mamoe.mirai.console.command.SystemCommandSender
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.isOperator
import org.meowcat.mesagisto.mirai.Plugin.Config
import org.meowcat.mesagisto.mirai.handlers.Receive

object Command : CompositeCommand(
  Plugin,
  primaryName = "msgist",
  secondaryNames = arrayOf("f"),
  description = "信使"
) {

  @SubCommand("bind")
  suspend fun MemberCommandSender.bind(roomAddress: String) {
    if (!user.isOperator()) {
      group.sendMessage("您不是管理员")
      return
    }
    when (val before = Config.bindings.put(group.id, roomAddress)){
      is String -> {
        Receive.change(before, roomAddress)
        group.sendMessage("成功将群聊: ${group.name} 的信使地址变更为$roomAddress")
      }
      null -> {
        Receive.add(roomAddress)
        group.sendMessage("成功将群聊: ${group.name} 的信使地址设置为$roomAddress")
      }
    }
  }
  @SubCommand("bind")
  suspend fun SystemCommandSender.bind(group: Group, roomAddress: String) {
    when (val before = Config.bindings.put(group.id, roomAddress)){
      is String -> {
        Receive.change(before, roomAddress)
        group.sendMessage("成功将群聊: ${group.name} 的信使地址变更为$roomAddress")
      }
      null -> {
        Receive.add(roomAddress)
        group.sendMessage("成功将群聊: ${group.name} 的信使地址设置为$roomAddress")
      }
    }
  }
  @SubCommand("unbind")
  suspend fun MemberCommandSender.unbind() {
    if (!user.isOperator()) {
      group.sendMessage("您不是管理员")
      return
    }
    val address = Config.bindings.remove(group.id) ?: return
    Receive.del(address)
    group.sendMessage("已解绑 ${group.name} 的信使频道")
  }

  @SubCommand("ban")
  suspend fun MemberCommandSender.ban(user: User) {
    if (!Config.perm.strict || !Config.perm.users.contains(user.id)) {
      group.sendMessage("信使的严格模式未启用 或 您不是本Mirai信使Bot的管理员")
      return
    }
    if (Config.blacklist.contains(user.id)) {
      group.sendMessage("${user.nick}-${user.id} 已经被封禁了")
    } else {
      Config.blacklist.add(user.id)
      group.sendMessage("已成功封禁：${user.nick}-${user.id}")
    }
  }

  @SubCommand("unban")
  suspend fun MemberCommandSender.unban(user: User) {
    if (!Config.perm.strict || !Config.perm.users.contains(user.id)) {
      group.sendMessage("信使的严格模式未启用 或 您不是本Mirai信使Bot的管理员")
      return
    }
    if (Config.blacklist.contains(user.id)) {
      Config.blacklist.remove(user.id)
      group.sendMessage("已成功解封：${user.nick}-${user.id}")
    } else {
      group.sendMessage("${user.nick}-${user.id} 没有被封禁")
    }
  }

  @SubCommand("status")
  suspend fun MemberCommandSender.status() {
    group.sendMessage("唔... 也许是在正常运行?")
  }

  @SubCommand("about")
  suspend fun MemberCommandSender.about() {
    group.sendMessage("GitHub项目主页 https://github.com/MeowCat-Studio/mesagisto")
  }

  @OptIn(ConsoleExperimentalApi::class)
  @SubCommand("disable")
  suspend fun MemberCommandSender.disable(@Name("group/channel") type: String) {
    if (!Config.perm.strict || !Config.perm.users.contains(user.id)) {
      group.sendMessage("信使的严格模式未启用 或 您不是本Mirai信使Bot的管理员")
      return
    }
    when (type) {
      "group" -> {
        if (Config.disableGroup.contains(group.id)) {
          group.sendMessage("此群组已经禁用过信使了")
          return
        }
        if (Config.bindings[group.id] == null) {
          group.sendMessage("此群组不存在信使频道，无需禁用")
          return
        }
        Config.disableGroup.add(group.id)
        group.sendMessage("已为此群组禁用信使")
      }
      "channel" -> {
        if (Config.bindings[group.id] == null) {
          group.sendMessage("此群组不存在信使频道，无需禁用")
          return
        }
        val channel: String = Config.bindings[group.id].toString()
        if (Config.disableChannel.contains(channel)) {
          group.sendMessage("已经在此频道mirai侧禁用过信使了")
          return
        }
        Config.disableChannel.add(channel)
        group.sendMessage("已为此频道mirai侧禁用信使")
      }
      else -> {
        // TODO: 2022/7/26
      }
    }
  }

  @OptIn(ConsoleExperimentalApi::class)
  @SubCommand("enable")
  suspend fun MemberCommandSender.enable(@Name("group/channel") type: String) {
    if (!Config.perm.strict || !Config.perm.users.contains(user.id)) {
      group.sendMessage("信使的严格模式未启用\n或 您不是本Mirai信使Bot的管理员")
      return
    }
    when (type) {
      "group" -> {
        if (Config.bindings[group.id] == null) {
          group.sendMessage("此群组不存在信使频道，无需操作")
          return
        }
        if (!Config.disableGroup.contains(group.id)) {
          group.sendMessage("此群组未禁用信使")
          return
        }
        Config.disableGroup.remove(group.id)
        group.sendMessage("已为此群组启用信使")
      }
      "channel" -> {
        if (Config.bindings[group.id] == null) {
          group.sendMessage("此群组不存在信使频道，无需操作")
          return
        }
        val channel: String = Config.bindings[group.id].toString()
        if (!Config.disableChannel.contains(channel)) {
          group.sendMessage("此频道Mirai侧未禁用信使")
          return
        }
        Config.disableChannel.remove(channel)
        group.sendMessage("此频道Mirai侧信使已解禁")
      }
      else -> {
        // TODO: 2022/7/26
      }
    }
  }
}
