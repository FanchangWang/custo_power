"""
name: 北京现代 APP 自动任务脚本
author: 原作者 https://github.com/xiaobu689/HhhhScripts 我修改了部分代码以适配青龙面板
env: BJXD="token1,token2,token3" // 北京现代 APP api token // 多个账号用英文 , 分割
env: HUNYUAN_API_KEY="sk-xxxx" // 腾讯混元AI APIKey

cron: 25 6 * * *
"""

import os
import random
import time
from datetime import datetime
import requests
from urllib3.exceptions import InsecureRequestWarning, InsecurePlatformWarning

requests.packages.urllib3.disable_warnings(InsecureRequestWarning)
requests.packages.urllib3.disable_warnings(InsecurePlatformWarning)


class RUN:
    name = "北京现代"

    def __init__(self, token):
        self.token = token
        self.pre_score = 0
        self.article_ids = []
        self.gpt_api_key = os.getenv("HUNYUAN_API_KEY")  # 混元 APIKey
        self.headers = {
            "Host": "bm2-api.bluemembers.com.cn",
            "token": token,
            "Accept": "*/*",
            "device": "android",
            "User-Agent": "okhttp/3.12.12",
            "App-Version": "8.26.1",
            "Origin-Id": "8ea51813bb38346e",
        }
        current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        self.push_content = f"运行时间: {current_time}\n"

    def add_push_content(self, content):
        self.push_content += content + "\n"

    def get_push_content(self):
        return self.push_content

    def user_info(self):
        url = "https://bm2-api.bluemembers.com.cn/v1/app/account/users/info"
        response_json = requests.get(url, headers=self.headers).json()
        if response_json["code"] == 0:
            nickname = response_json["data"]["nickname"]
            phone = response_json["data"]["phone"]
            score_value = response_json["data"]["score_value"]
            self.pre_score = score_value
            print(f"👻 用户名: {nickname} | 手机号: {phone} | 积分: {score_value}")
            self.add_push_content(
                f"👻 用户名: {nickname} | 手机号: {phone} | 积分: {score_value}"
            )
            return True
        else:
            print(f"❌ 获取用户信息失败， token 已失效，请重新抓包")
            self.add_push_content(f"❌ 获取用户信息失败， token 已失效，请重新抓包")
            return False

    def user_score_info(self):
        url = "https://bm2-api.bluemembers.com.cn/v1/app/account/users/info"
        response_json = requests.get(url, headers=self.headers).json()
        if response_json["code"] == 0:
            nickname = response_json["data"]["nickname"]
            phone = response_json["data"]["phone"]
            score_value = response_json["data"]["score_value"]
            diff_score = score_value - self.pre_score
            print(
                f"👻 用户名: {nickname} | 用户: {phone} | 总积分: {score_value} | 本次运行新增积分: {diff_score}"
            )
            self.add_push_content(
                f"👻 用户名: {nickname} | 手机号: {phone} | 总积分: {score_value} | 本次运行新增积分: {diff_score}"
            )

    def do_task(self):
        url = "https://bm2-api.bluemembers.com.cn/v1/app/user/task/list"
        response_json_ = requests.get(url, headers=self.headers).json()
        print("get_task_list response_json_=", response_json_)
        if response_json_["code"] == 0:
            # 获取 data 字段
            actions = response_json_.get("data", {})

            if "action4" in actions:  # 签到任务
                if actions["action4"].get("status") == 1:
                    print("✅ 签到任务 已完成，跳过")
                    self.add_push_content("✅ 签到任务 已完成，跳过")
                else:
                    print("签到任务 未完成，开始执行任务")
                    self.add_push_content("签到任务 未完成，开始执行任务")
                    self.do_sign()
                    time.sleep(random.randint(10, 15))
            else:
                print("❌ task list action4 签到任务 不存在")
                self.add_push_content("❌ task list action4 签到任务 不存在")

            if "action12" in actions:  # 浏览文章任务
                if actions["action12"].get("status") == 1:
                    print("✅ 浏览文章任务 已完成，跳过")
                    self.add_push_content("✅ 浏览文章任务 已完成，跳过")
                else:
                    print("浏览文章任务 未完成，开始执行任务")
                    self.add_push_content("浏览文章任务 未完成，开始执行任务")
                    self.article_list()
                    for i in range(3):
                        self.view_article()
                        time.sleep(random.randint(10, 15))
                    self.article_score_add()
                    time.sleep(random.randint(5, 10))
            else:
                print("❌ task list action12 浏览文章任务 不存在")
                self.add_push_content("❌ task list action12 浏览文章任务 不存在")

            if "action39" in actions:  # 答题任务
                if actions["action39"].get("status") == 1:
                    print("✅ 答题任务 已完成，跳过")
                    self.add_push_content("✅ 答题任务 已完成，跳过")
                else:
                    print("答题任务 未完成，开始执行任务")
                    self.add_push_content("答题任务 未完成，开始执行任务")
                    self.daily_question()
            else:
                print("❌ task list action39 答题任务 不存在")
                self.add_push_content("❌ task list action39 答题任务 不存在")
        else:
            print(f'❌ 获取任务列表失败， {response_json_["msg"]}')
            self.add_push_content(f'❌ 获取任务列表失败， {response_json_["msg"]}')

    def do_sign(self):
        score = 0
        hid = ""
        url = "https://bm2-api.bluemembers.com.cn/v1/app/user/reward_list"
        while score < 5:
            response_json_ = requests.get(url, headers=self.headers).json()
            print("do_sign response_json_=", response_json_)
            if response_json_["code"] == 0:
                hid = response_json_["data"]["hid"]
                rewardHash = response_json_["data"]["rewardHash"]
                list = response_json_["data"]["list"]
                for item in list:
                    if item["hid"] == hid:
                        score = item["score"]
                        if score >= 5:
                            print(f"tip: 如果签到成功, 积分+{score}")
                            self.sign(hid, rewardHash, score)
                            break
                        else:
                            print(
                                f"预计签到成功，可得{score}积分，太低不签，重新初始化！随机延时 20-30s"
                            )
            else:
                print(f'❌ 获取签到列表失败， {response_json_["msg"]}')
                self.add_push_content(f'❌ 获取签到列表失败， {response_json_["msg"]}')
                break
            time.sleep(random.randint(20, 30))

    def sign(self, hid, rewardHash, score):
        # 状态上报
        json_data = {
            "hid": hid,
            "hash": rewardHash,
            "sm_deviceId": "",
            "ctu_token": None,
        }
        url = "https://bm2-api.bluemembers.com.cn/v1/app/user/reward_report"
        response_json_ = requests.post(url, headers=self.headers, json=json_data).json()
        print("article_list response_json_=", response_json_)
        if response_json_["code"] == 0:
            print(f"✅ 签到成功 | 积分+{score}")
            self.add_push_content(f"✅ 签到成功 | 积分+{score}")
        else:
            print(f'❌ 签到失败， {response_json_["msg"]}')
            self.add_push_content(f'❌ 签到失败， {response_json_["msg"]}')

    # 浏览3篇文章5积分
    def view_article(self):
        article_id = random.choice(self.article_ids)
        self.article_ids.remove(article_id)
        print(f"浏览文章 | 文章ID: {article_id}")
        url = f"https://bm2-api.bluemembers.com.cn/v1/app/white/article/detail_app/{article_id}"
        requests.get(url, headers=self.headers)

    def article_list(self):
        params = {
            "page_no": "1",
            "page_size": "20",
            "type_hid": "",
        }
        url = "https://bm2-api.bluemembers.com.cn/v1/app/white/article/list2"
        response_json_ = requests.get(url, params=params, headers=self.headers).json()
        print("article_list response_json=", response_json_)
        if response_json_["code"] == 0:
            list = response_json_["data"]["list"]
            for item in list:
                article_id = item["hid"]
                self.article_ids.append(article_id)
        else:
            print(f'❌ 获取文章列表失败， {response_json_["msg"]}')
            self.add_push_content(f'❌ 获取文章列表失败， {response_json_["msg"]}')

    def article_score_add(self):
        json_data = {
            "ctu_token": "",
            "action": 12,
        }
        url = "https://bm2-api.bluemembers.com.cn/v1/app/score"
        response_json_ = requests.post(url, headers=self.headers, json=json_data).json()
        print("article_score_add response_json=", response_json_)
        if response_json_["code"] == 0:
            score = response_json_["data"]["score"]
            print(f"✅ 浏览文章成功 | 积分+{score}")
            self.add_push_content(f"✅ 浏览文章成功 | 积分+{score}")
        else:
            print(f'❌ 浏览文章失败， {response_json_["msg"]}')
            self.add_push_content(f'❌ 浏览文章失败， {response_json_["msg"]}')

    # 每日问答
    def daily_question(self):
        question_str = ""
        today_date = datetime.now().strftime("%Y%m%d")
        params = {
            "date": today_date,
        }
        url = "https://bm2-api.bluemembers.com.cn/v1/app/special/daily/ask_info"
        response_json = requests.get(url, params=params, headers=self.headers).json()
        print("daily_question response_json=", response_json)
        if response_json["code"] == 0:
            question_info = response_json["data"]["question_info"]
            questions_hid = question_info["questions_hid"]
            # 题目
            question = question_info["content"]
            print(question)
            question_str += f"{question}\n"
            # 选项
            options = question_info["option"]
            for option in options:
                option_content = option["option_content"]
                print(f'{option["option"]}. {option_content}')
                question_str += f'{option["option"]}. {option_content}\n'

            answer = self.get_answer(question_str)
            time.sleep(random.randint(5, 10))

            self.answer_question(questions_hid, answer)

    def get_gpt_answer(self, content):
        choice_base_desc = (
            "这是一个选择题，请严格按照以下格式回答：芝麻开门#你的答案#芝麻开门\n"
        )
        url = "https://api.hunyuan.cloud.tencent.com/v1/chat/completions"
        headers = {
            "Authorization": f"Bearer {self.gpt_api_key}",
            "Content-Type": "application/json",
        }
        json_data = {
            "model": "hunyuan-turbo",
            "messages": [{"role": "user", "content": f"{choice_base_desc}{content}"}],
        }
        try:
            response = requests.post(url, headers=headers, json=json_data)
            response.raise_for_status()  # 检查响应状态码，如果不是 200，抛出 HTTPError
            response_json = response.json()
            extracted_content = response_json["choices"][0]["message"]["content"]
            parts = extracted_content.split("#")
            if len(parts) >= 3 and parts[1] in ["A", "B", "C", "D"]:
                return parts[1]
            else:
                print(f"腾讯混元AI 无效的答案: {extracted_content}")
        except requests.exceptions.RequestException as e:
            print(f"腾讯混元AI 请求失败: {e}")
        except ValueError as e:
            print(f"腾讯混元AI JSON 解析失败: {e}")
        except KeyError as e:
            print(f"腾讯混元AI 键不存在: {e}")
        return ""

    def get_answer(self, question_str):
        if self.gpt_api_key:
            answer = self.get_gpt_answer(question_str)
            print(f"本次使用GPT回答，GPT给出的答案是：{answer}")
            if answer == "":
                answer = random.choice(["A", "B", "C", "D"])
            return answer
        else:
            answer = random.choice(["A", "B", "C", "D"])
            print(f"本次盲答, 随机选出的答案是: {answer}")
            return answer

    def answer_question(self, questions_hid, my_answer):
        print("开始答题")
        json_data = {
            "answer": my_answer,
            "questions_hid": questions_hid,
            "ctu_token": "",
        }
        url = "https://bm2-api.bluemembers.com.cn/v1/app/special/daily/ask_answer"
        response_json_ = requests.post(url, headers=self.headers, json=json_data).json()
        print("answer_question response_json=", response_json_)
        # response_json= {'code': 0, 'data': {'answer': '', 'answer_score': '', 'state': 3}, 'msg': '', 'title': ''}
        if response_json_["code"] == 0:
            if response_json_["data"]["state"] == 3:
                print("❌ 回答错误")
                self.add_push_content("❌ 答题错误")
            elif response_json_["data"]["state"] == 2:
                answer = response_json_["data"]["answer"]  # C.造价低
                print("answer=", answer)
                score = response_json_["data"]["answer_score"]
                print("score=", score)
                print(f"✅ 答题正确 | 积分+{score}")
                self.add_push_content(f"✅ 答题正确 | 积分+{score}")
        else:
            print(f'❌ 答题失败, msg: {response_json_["msg"]}')
            self.add_push_content(f'❌ 答题失败, msg: {response_json_["msg"]}')

    def main(self):
        if self.user_info():
            self.do_task()  # 根据任务列表完成请求自动执行任务

            # 单独执行 签到任务
            # self.do_sign()
            # time.sleep(random.randint(10, 15))

            # 单独执行 浏览文章任务
            # self.article_list()
            # for i in range(3):
            #     self.view_article()
            #     time.sleep(random.randint(10, 15))
            # self.article_score_add()
            # time.sleep(random.randint(5, 10))

            # 单独执行 答题任务
            # self.daily_question()

            self.user_score_info()  # 获取积分信息 统计本次运行新增的积分
        return self.get_push_content()


if __name__ == "__main__":
    env_name = "BJXD"
    tokenStr = os.getenv(env_name)
    if not tokenStr:
        print(f"⛔️ 未获取到ck变量：请检查变量 {env_name} 是否填写")
        exit(0)

    push_title = "北京现代 APP 自动任务"
    push_content = ""

    tokens = tokenStr.split(",")

    print(f"共获取到 tokens: {len(tokens)} 个")
    push_content += f"共获取到 tokens: {len(tokens)} 个\n"

    # 循环打印每个元素
    for i, token in enumerate(tokens, start=1):
        if i > 1:
            print("\n随机等待 10-15s 进行下一个账号")
            time.sleep(random.randint(10, 15))
        print(f"\n======== ▷ 第 {i} 个账号 ◁ ========")
        push_content += f"\n======== ▷ 第 {i} 个账号 ◁ ========\n"
        push_content += RUN(token).main() + "\n"
    try:
        QLAPI.notify(push_title, push_content)
    except NameError:
        print(push_title, "\n", push_content)
