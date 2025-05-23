import sys

student_num = sys.argv[1]
password = sys.argv[2]

from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
from datetime import datetime
import shutil
import json
import tempfile
import os

chrome_options = Options()
chrome_options.add_argument("--headless")
chrome_options.add_argument("--no-sandbox")
chrome_options.add_argument("--disable-dev-shm-usage")

# 👇 고유한 user-data-dir을 임시 폴더로 지정
user_data_dir = tempfile.mkdtemp()
chrome_options.add_argument(f"--user-data-dir={user_data_dir}")

driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

driver.get('https://eclass.hufs.ac.kr/ilos/main/main_form.acl')

# 로그인 버튼 클릭
try:
    login_button = WebDriverWait(driver, 10).until(
        EC.element_to_be_clickable((By.CSS_SELECTOR, 'li.header_login.login-btn-color'))
    )
    login_button.click()
except Exception as e:
    print('로그인 버튼 클릭 실패:', e)
    driver.quit()
    exit()

try:
    WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.NAME, 'usr_id')))
except:
    print("로그인 폼 로딩 실패")
    driver.quit()
    exit()

id_input = driver.find_element(By.NAME, 'usr_id')
pw_input = driver.find_element(By.NAME, 'usr_pwd')
id_input.send_keys(student_num)
pw_input.send_keys(password)

# --- 5. 로그인 완료 ---
login_submit_button = driver.find_element(By.CSS_SELECTOR, 'input[type="image"]')
login_submit_button.click()
print("[DEBUG] 로그인 후 URL:", driver.current_url, file=sys.stderr)
time.sleep(3)

# --- 6. 정규/비정규 과목명 가져오기 ---
regular_courses = []
non_regular_courses = []
is_regular = True

li_elements = driver.find_elements(By.CSS_SELECTOR, 'ol > li')
print("[DEBUG] li 개수:", len(li_elements), file=sys.stderr)
for li in li_elements:
    print("[DEBUG] li text:", li.text.strip(), file=sys.stderr)
    if 'term_info' in li.get_attribute('class'):
        if li.text.strip() == '비정규과목':
            is_regular = False
        continue

    try:
        em_tag = li.find_element(By.CSS_SELECTOR, 'em.sub_open')
        title_attr = em_tag.get_attribute('title')
        if title_attr:
            course_name = title_attr.replace(' 강의실 들어가기', '').strip()
            if is_regular:
                regular_courses.append(course_name)
            else:
                non_regular_courses.append(course_name)
    except:
        continue

# --- 7. 달력에서 과목일정(dot) 있는 날짜 추출 ---
today = datetime.today()
today_day = 1

schedule_days = []

calendar_cells = driver.find_elements(By.CSS_SELECTOR, 'table.main-Schedule td')

one_day_index = -1
cell_day_list = []

# 1. 먼저 모든 셀에서 날짜 수집 + 1일 위치 확인
for idx, cell in enumerate(calendar_cells):
    try:
        day_text = cell.find_element(By.CSS_SELECTOR, 'div.day').text.strip()
        numeric_day = int(''.join(filter(str.isdigit, day_text)))
        cell_day_list.append((idx, numeric_day))

        if numeric_day == 1 and one_day_index == -1:
            one_day_index = idx
    except:
        cell_day_list.append((idx, -1))

# 2. 다시 셀 순회: 1일 이후 + 오늘 이후 + 일정이 있는 셀만
for idx, cell in enumerate(calendar_cells):
    if idx < one_day_index:
        continue  # 저번 달

    try:
        day_text = cell.find_element(By.CSS_SELECTOR, 'div.day').text.strip()
        numeric_day = int(''.join(filter(str.isdigit, day_text)))

        # 일정 dot 확인: title이 '과목일정' 또는 '개인일정'인 div가 존재하면 dot 있음
        has_dot = False
        inner_divs = cell.find_elements(By.CSS_SELECTOR, 'div[title]')
        for div in inner_divs:
            if div.get_attribute('title') in ['과목일정', '개인일정']:
                has_dot = True
                break

        if has_dot and numeric_day >= today_day:
            schedule_days.append(str(numeric_day))

    except:
        continue

print("[DEBUG] 달력 셀 수:", len(calendar_cells), file=sys.stderr)
print("[DEBUG] schedule_days:", schedule_days, file=sys.stderr)

# --- 8. 일정 데이터 수집 시작 ---
schedule_infos = []

for day_text in schedule_days:
    try:
        # 매번 새로 달력 셀 읽기
        calendar_cells = driver.find_elements(By.CSS_SELECTOR, 'table.main-Schedule td')

        clicked = False

        for cell in calendar_cells:
            try:
                day = cell.find_element(By.CSS_SELECTOR, 'div.day').text.strip()

                if day == day_text.replace('월', '').replace('일', '').strip():
                    cell.click()

                    WebDriverWait(driver, 10).until(
                        EC.presence_of_element_located((By.CSS_SELECTOR, '.schedule_txt_view div[id^="view_"]'))
                    )
                    clicked = True
                    break

            except:
                continue

        if not clicked:
            continue

        # 셀 클릭 성공하면 과제 긁기
        task_elements = driver.find_elements(By.XPATH, '//*[starts-with(@id, "change_")]')
        print("[DEBUG] task_elements 수:", len(task_elements), file=sys.stderr)

        for task_elem in task_elements:
            try:
                task_id_full = task_elem.get_attribute('id')  # ex) change_0_st_2_12050753 or change_0_All_3_26485
                parts = task_id_full.split('_')

                index = parts[1]
                source = parts[2]  # 'st' or 'All'
                kind = parts[3]  # '2': 일반 과제, '3': 개인일정
                task_id = parts[4]

                # 보이기 버튼 클릭 시도
                show_button_id = f"show_{index}_{source}_{kind}_{task_id}"
                try:
                    show_button = driver.find_element(By.ID, show_button_id)
                    if show_button.is_displayed():
                        show_button.click()
                        time.sleep(0.5)
                except Exception as e:
                    print(f'[경고] 보이기 버튼 클릭 실패 (무시): {e}')

                view_id = f"view_{index}_{source}_{kind}_{task_id}"
                detail_box = driver.find_element(By.ID, view_id)

                # --- 케이스 분기 ---
                if kind == '2':
                    # 일반 과제형 (기존)
                    task_name_elem = task_elem.find_element(By.XPATH, f'//*[@id="{task_id_full}"]/div/span')
                    task_name = task_name_elem.text.strip()

                    subject_name_elem = driver.find_element(By.XPATH, f'//*[@id="{view_id}"]/a/div[1]')
                    subject_name = subject_name_elem.text.strip()

                    deadline_elem = driver.find_element(By.XPATH, f'//*[@id="{view_id}"]/a/div[2]')
                    deadline = deadline_elem.text.replace('마감일 :', '').strip()

                    divs_in_box = detail_box.find_elements(By.CSS_SELECTOR, 'div')
                    status = ''
                    for div in divs_in_box:
                        if '상태 :' in div.text:
                            status = div.text.replace('상태 :', '').strip()
                            break


                elif kind == '3':

                    # 개인일정형

                    subject_name = detail_box.find_element(By.XPATH, f'//*[@id="{view_id}"]/div[1]').text.strip()

                    task_name = detail_box.find_element(By.XPATH, f'//*[@id="{view_id}"]/div[3]').text.strip()

                    # day_text 기준 날짜 생성 (현재 연도, 월 + day_text일자)

                    try:

                        selected_day = int(day_text)

                        now = datetime.now()

                        deadline_dt = datetime(now.year, now.month, selected_day, 11, 30)

                        deadline = deadline_dt.strftime('%Y-%m-%d %H:%M')

                    except Exception as e:

                        print(f'[개인일정 마감일 오류] {e}')

                        deadline = '개인일정'

                    status = '미제출'
                else:
                    continue  # 기타 종류 무시

                # 결과 저장
                schedule_infos.append({
                    '과제 제목': task_name,
                    '과목명': subject_name,
                    '마감일': deadline,
                    '상태': status
                })

            except Exception as e:
                print('일정 파싱 실패:', e)
                continue


    except Exception as e:
        print('날짜 클릭 실패:', e)
        continue

# --- 9. 최종 결과 출력 ---
print(f"[DEBUG] 수집된 과제 수: {len(schedule_infos)}", file=sys.stderr)
print(json.dumps(schedule_infos, ensure_ascii=False))


# --- 10. 드라이버 종료 ---
driver.quit()
shutil.rmtree(user_data_dir, ignore_errors=True)