/*
 * Copyright 2011-2012 Institute of Information Engineering and Management,
 * Information & Market Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.kit.im.enums

public enum ConsumptionType {
  MIN5(5), // 5 minute interval
  MIN15(15), // 15 minute interval
  MIN30(30), // 30 minutes interval
  H3(180) // 3 hour interval

  private final int minutes

  ConsumptionType(int minutes) {
    this.minutes = minutes
  }

  public int minutes() {
    return minutes
  }

  public int getId() {
    return minutes
  }
}